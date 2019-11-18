package com.wlkg.common.advice;


import com.wlkg.common.enums.ExceptionEnums;
import com.wlkg.common.exception.WlkgException;
import com.wlkg.common.vo.ExceptionResult;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class CommonExceptionHandler {

    @ExceptionHandler(WlkgException.class)
    public ResponseEntity<ExceptionResult> handlerException(WlkgException e){
        ExceptionEnums exceptionEnums = e.getExceptionEnums();
        ExceptionResult exceptionResult = new ExceptionResult(exceptionEnums);
        return ResponseEntity.status(exceptionResult.getStatus()).body(exceptionResult);
    }
}
