package com.LetsMeet.LetsMeet.Root.Controller;

import org.apache.tomcat.util.http.fileupload.impl.FileSizeLimitExceededException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    /**
     * Handle exception when uploaded files are larger than allowed (set in properties). Redirects to the previous page and
     * displays error message {"danger":"message"}.
     */
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public final String fileSize(MaxUploadSizeExceededException ex, WebRequest request, RedirectAttributes redirectAttributes){
        String message = String.format("The file is too big. Maximum size: %d",ex.getMaxUploadSize());
        redirectAttributes.addFlashAttribute("danger",message);

        var referer = request.getHeader("Referer");
        return "redirect:"+referer;
    }
}
