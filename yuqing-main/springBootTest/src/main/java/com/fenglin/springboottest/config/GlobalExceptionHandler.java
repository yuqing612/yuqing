package com.fenglin.springboottest.config;

import com.fenglin.springboottest.common.Result;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

/**
 * 全局异常处理：业务异常转 400，其余转 500，保证前端始终拿到统一结构。
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public Result<Void> handleBizException(IllegalArgumentException e) {
        return Result.fail(e.getMessage());
    }

    /** @Valid 校验失败（请求体 DTO） */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result<Void> handleValidException(MethodArgumentNotValidException e) {
        String msg = e.getBindingResult().getFieldErrors().stream()
                .map(f -> ((FieldError) f).getField() + "：" + f.getDefaultMessage())
                .collect(Collectors.joining("；"));
        return Result.fail(msg.isBlank() ? "参数校验失败" : msg);
    }

    /** 方法参数上的约束校验失败（@RequestParam / @PathVariable 等） */
    @ExceptionHandler(ConstraintViolationException.class)
    public Result<Void> handleConstraintViolation(ConstraintViolationException e) {
        return Result.fail("参数校验失败：" + e.getMessage());
    }

    /** 请求体 JSON 格式错误 / 无法解析 */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public Result<Void> handleNotReadable(HttpMessageNotReadableException e) {
        return Result.fail("请求体格式错误，请检查 JSON");
    }

    @ExceptionHandler(Exception.class)
    public Result<Void> handleException(Exception e) {
        return Result.error("服务器开小差了：" + e.getMessage());
    }
}
