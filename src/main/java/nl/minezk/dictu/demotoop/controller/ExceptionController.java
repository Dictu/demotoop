package nl.minezk.dictu.demotoop.controller;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.NoHandlerFoundException;

import nl.minezk.dictu.demotoop.model.ToopException;

@ControllerAdvice
public class ExceptionController {

	private static final String MODEL = "model";
	private static final String ERRORCODE = "errorcode";
	private static final String HTTPSTATUS = "httpstatus";
	private static final String EXCEPTION = "exception";

	private static final Logger LOGGER = LoggerFactory.getLogger(ExceptionController.class);
	
	@ExceptionHandler(ToopException.class)
	@ResponseStatus(value=HttpStatus.BAD_REQUEST)
    public ModelAndView handleToopException(Exception ex) {
		return handleException(ex, HttpStatus.BAD_REQUEST, true);
    }
	
	@ExceptionHandler(NoHandlerFoundException.class)
	@ResponseStatus(value=HttpStatus.NOT_FOUND)
    public ModelAndView handleNoHandlerFoundException(NoHandlerFoundException ex) {
		return handleException(ex, HttpStatus.NOT_FOUND, false);
    }
	
	@ExceptionHandler(Exception.class)
	@ResponseStatus(value=HttpStatus.BAD_REQUEST)
    public ModelAndView handleException(Exception ex) {
		return handleException(ex, HttpStatus.BAD_REQUEST, true);
    }

	private ModelAndView handleException(Exception ex, HttpStatus status, boolean logStackTrace) {
		return handleException(ex, ex.getMessage(), status, logStackTrace);
	}
	
	/**
	 * Handles given exception, logs it and returns error ModelAndView with information for user.
	 * Also invalidates session.
	 * @param ex
	 * @param message
	 * @param status
	 * @return
	 */
	private ModelAndView handleException(Exception ex, String message, HttpStatus status, boolean isError) {
		ConcurrentMap<String, Object> m = new ConcurrentHashMap<String, Object>();
		if (isError) {
			//Generate code to be shown on oops page and in logging.
			String code = UUID.randomUUID().toString().split("-")[0];
			m.put(ERRORCODE, code);
			LOGGER.error("handle {} ({})", ex.getClass().getSimpleName(), code, ex);
		}
		else {
			m.put(ERRORCODE, "");
			LOGGER.warn("handle {}", ex.getClass().getSimpleName());
		}
		if (null == message) {
			message = "";
		}
		m.put(EXCEPTION, message);
		m.put(HTTPSTATUS, status);		
        return new ModelAndView("oops", MODEL, m);
	}
}
