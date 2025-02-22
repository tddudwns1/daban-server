package com.jamongdan.daban._common.response.error.exception;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.jamongdan.daban._common.response.error.ErrorCode;
import com.jamongdan.daban._common.response.error.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.TypeMismatchException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.io.IOException;

import static com.jamongdan.daban._common.response.error.ErrorCode.NOT_VALID_ERROR;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * [Exception] API 호출 시 '객체' 혹은 '파라미터' 데이터 값이 유효하지 않은 경우
     *
     * @param ex MethodArgumentNotValidException
     * @return ResponseEntity<ErrorResponse>
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    protected ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        log.error("handleMethodArgumentNotValidException", ex);
        BindingResult bindingResult = ex.getBindingResult();
        StringBuilder stringBuilder = new StringBuilder();
        for (FieldError fieldError : bindingResult.getFieldErrors()) {
            stringBuilder.append(fieldError.getField()).append(":");
            stringBuilder.append(fieldError.getDefaultMessage());
            stringBuilder.append(", ");
        }

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ErrorResponse.of(NOT_VALID_ERROR, String.valueOf(stringBuilder)));
    }

    /**
     * [Exception] API 호출 시 'Header' 내에 데이터 값이 유효하지 않은 경우
     *
     * @param ex MissingRequestHeaderException
     * @return ResponseEntity<ErrorResponse>
     */
    @ExceptionHandler(MissingRequestHeaderException.class)
    protected ResponseEntity<ErrorResponse> handleMissingRequestHeaderException(MissingRequestHeaderException ex) {
        log.error("MissingRequestHeaderException", ex);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ErrorResponse.of(ErrorCode.REQUEST_BODY_MISSING_ERROR, ex.getMessage()));
    }

    /**
     * [Exception] 클라이언트에서 Body로 '객체' 데이터가 넘어오지 않았을 경우
     *
     * @param ex HttpMessageNotReadableException
     * @return ResponseEntity<ErrorResponse>
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    protected ResponseEntity<ErrorResponse> handleHttpMessageNotReadableException(
            HttpMessageNotReadableException ex) {
        log.error("HttpMessageNotReadableException", ex);

        return ResponseEntity
                .badRequest()
                .body(ErrorResponse.of(ErrorCode.REQUEST_BODY_MISSING_ERROR, ex.getMessage()));
    }

    /**
     * [Exception] 클라이언트에서 request로 '파라미터로' 데이터가 넘어오지 않았을 경우
     *
     * @param ex MissingServletRequestParameterException
     * @return ResponseEntity<ErrorResponse>
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    protected ResponseEntity<ErrorResponse> handleMissingRequestHeaderExceptionException(
            MissingServletRequestParameterException ex) {
        log.error("handleMissingServletRequestParameterException", ex);

        return ResponseEntity
                .badRequest()
                .body(ErrorResponse.of(ErrorCode.MISSING_REQUEST_PARAMETER_ERROR, ex.getMessage()));
    }

    /**
     * [Exception] 지원되지 않는 HTTP 메서드로 요청한 경우
     *
     * @param ex HttpRequestMethodNotSupportedException
     * @return ResponseEntity<ErrorResponse>
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    protected ResponseEntity<ErrorResponse> handleHttpRequestMethodNotSupportedException(
            HttpRequestMethodNotSupportedException ex) {
        log.error("handleHttpRequestMethodNotSupportedException", ex);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ErrorResponse.of(ErrorCode.METHOD_NOT_SUPPORTED_ERROR, ex.getMessage()));
    }

    /**
     * [Exception] 요청 경로의 Path Variable이 누락된 경우
     *
     * @param e MissingPathVariableException
     * @return ResponseEntity<ErrorResponse>
     */
    @ExceptionHandler(MissingPathVariableException.class)
    protected ResponseEntity<ErrorResponse> handleMissingPathVariableException(
            MissingPathVariableException e) {
        log.error("handleMissingPathVariableException", e);

        return ResponseEntity
                .badRequest()
                .body(ErrorResponse.of(ErrorCode.PATH_VARIABLE_NOT_FOUND_ERROR, e.getMessage()));
    }

    /**
     * [Exception] 잘못된 서버 요청일 경우 발생한 경우
     *
     * @param e HttpClientErrorException
     * @return ResponseEntity<ErrorResponse>
     */
    @ExceptionHandler(HttpClientErrorException.BadRequest.class)
    protected ResponseEntity<ErrorResponse> handleBadRequestException(HttpClientErrorException e) {
        log.error("HttpClientErrorException.BadRequest", e);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ErrorResponse.of(ErrorCode.BAD_REQUEST_ERROR, e.getMessage()));
    }

    /**
     * [Exception] 잘못된 주소로 요청 한 경우
     *
     * @param e NoHandlerFoundException
     * @return ResponseEntity<ErrorResponse>
     */
    @ExceptionHandler(value = {
            NoHandlerFoundException.class,
            TypeMismatchException.class})
    protected ResponseEntity<ErrorResponse> handleNoHandlerFoundExceptionException(NoHandlerFoundException e) {
        log.error("handleNoHandlerFoundExceptionException", e);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ErrorResponse.of(ErrorCode.NOT_FOUND_ERROR, e.getMessage()));
    }

    /**
     * [Exception] NULL 값이 발생한 경우
     *
     * @param e NullPointerException
     * @return ResponseEntity<ErrorResponse>
     */
    @ExceptionHandler(NullPointerException.class)
    protected ResponseEntity<ErrorResponse> handleNullPointerException(NullPointerException e) {
        log.error("handleNullPointerException", e);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ErrorResponse.of(ErrorCode.NULL_POINT_ERROR, e.getMessage()));
    }

    /**
     * Input / Output 내에서 발생한 경우
     *
     * @param ex IOException
     * @return ResponseEntity<ErrorResponse>
     */
    @ExceptionHandler(IOException.class)
    protected ResponseEntity<ErrorResponse> handleIOException(IOException ex) {
        log.error("handleIOException", ex);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ErrorResponse.of(ErrorCode.IO_ERROR, ex.getMessage()));
    }

    /**
     * com.fasterxml.jackson.core 내에 Exception 발생하는 경우
     *
     * @param ex JsonProcessingException
     * @return ResponseEntity<ErrorResponse>
     */
    @ExceptionHandler(JsonProcessingException.class)
    protected ResponseEntity<ErrorResponse> handleJsonProcessingException(JsonProcessingException ex) {
        log.error("handleJsonProcessingException", ex);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ErrorResponse.of(ErrorCode.REQUEST_BODY_MISSING_ERROR, ex.getMessage()));
    }

    // ==================================================================================================================

    /**
     * BusinessException에서 발생한 에러
     *
     * @param ex BusinessException
     * @return ResponseEntity
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusinessException(BusinessException ex) {
        log.error("Exception", ex);

        return ResponseEntity
                .badRequest()
                .body(ErrorResponse.of(ex.getErrorCode()));
    }

    /**
     * [Exception] 모든 Exception 경우 발생
     *
     * @param ex Exception
     * @return ResponseEntity<ErrorResponse>
     */
    @ExceptionHandler(Exception.class)
    protected final ResponseEntity<ErrorResponse> handleAllExceptions(Exception ex) {
        log.error("Exception", ex);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ErrorResponse.of(ErrorCode.INTERNAL_SERVER_ERROR, ex.getMessage()));
    }
}
