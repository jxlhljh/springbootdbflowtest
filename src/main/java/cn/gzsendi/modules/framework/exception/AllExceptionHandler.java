package cn.gzsendi.modules.framework.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import cn.gzsendi.modules.framework.constants.RespCodeConstant;
import cn.gzsendi.modules.framework.model.Result;

@RestControllerAdvice
public class AllExceptionHandler {
	
	private static Logger logger = LoggerFactory.getLogger(AllExceptionHandler.class);
	
    @ExceptionHandler(value = Exception.class)
    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    public Result<Object> defaultErrorHandler(Exception e) {
        logger.error("AllExceptionHandler ", e);

        if (e instanceof org.springframework.web.servlet.NoHandlerFoundException) {
            //404 Not Found
            return Result.error(404, e.toString());
        } else {
            //500
        	return Result.error(RespCodeConstant.INTERNAL_SERVER_ERROR_500, e.toString());
        }
    }

}
