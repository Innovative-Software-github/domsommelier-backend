package com.innovativesoftware.domsommelier_backend.auth_management;

import com.innovativesoftware.domsommelier_backend.customer_management.customer.entity.Customer;
import com.innovativesoftware.domsommelier_backend.customer_management.customer_recommendations.repository.CustomerRepository;
import com.innovativesoftware.domsommelier_backend.infrastructure.RedisService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final CustomerRepository customerRepository;
    private final RedisService redisService;
    private final JwtTokenProvider jwtTokenProvider;
    private final SimpleMailMessage simpleMailMessage;
    private final JavaMailSender mailSender;

    private static final String OTP_PREFIX = "auth:otp:";

    /**
     * Шаг 1: Инициация входа
     */
    public void initiateLogin(String email) {
        // 1. Проверяем наличие пользователя
        if (!customerRepository.existsByEmail(email)) {
            Customer customer = new Customer();
            customer.setEmail(email);
            customerRepository.save(customer);
        }
//        customerRepository.findByEmail(email).;
//                .orElseThrow(() -> new RuntimeException("Пользователь с таким email не найден"));

        String redisKey = OTP_PREFIX + email;

        // 2. Проверка защиты от спама (через Redis)
        if (redisService.hasKey(redisKey)) {
            OtpSession session = redisService.getObject(redisKey, OtpSession.class);
            if (session == null) {
                throw new RuntimeException("OTP сессия не найдена или повреждена");
            }
            if (session.getLastSentTime().plusMinutes(1).isAfter(LocalDateTime.now())) {
                throw new RuntimeException("Пожалуйста, подождите 1 минуту перед повторной отправкой кода.");
            }
        }

        // 3. Генерация кода
        String code = String.format("%04d", new Random().nextInt(10000));

        // 4. Сохранение в Redis
        OtpSession newSession = new OtpSession(code, LocalDateTime.now());

        // Используем метод save(key, object)
        redisService.save(redisKey, newSession);

        // Устанавливаем срок жизни 5 минут
        redisService.setExpire(redisKey, 5, TimeUnit.MINUTES);

        // 5. Отправка Email (заглушка)
        sendEmailStub(email, code);
    }

    /**
     * Шаг 2: Подтверждение
     */
    public AuthModels.AuthResponse confirmLogin(String email, String code) {
        String redisKey = OTP_PREFIX + email;

        // Достаем объект
        OtpSession session = redisService.getObject(redisKey, OtpSession.class);

        if (session == null) {
            throw new BadCredentialsException("Код не найден или срок действия истек");
        }

        if (!session.getCode().equals(code)) {
            throw new BadCredentialsException("Неверный код");
        }

        // Если успех — удаляем код из Redis, чтобы нельзя было использовать повторно
        redisService.remove(redisKey);

        Customer customer = customerRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        String token = jwtTokenProvider.createToken(customer);

        return AuthModels.AuthResponse.builder()
                .customerId(customer.getId().toString())
                .token(token)
                .firstName(customer.getFirstName())
                .secondName(customer.getSecondName())
                .build();
    }

    private void sendEmailStub(String email, String code) {
        SimpleMailMessage msg = new SimpleMailMessage(simpleMailMessage);
        msg.setTo(email);
        msg.setText(
                "Ваш код для входа в Wine Service: " + code + "\n\nДействителен 5 минут.");
        try {
            mailSender.send(msg);
        }
        catch (MailException ex) {
            log.error("Проблема с отправкой email {}", ex.getMessage());
            throw ex;
        }
    }
}
