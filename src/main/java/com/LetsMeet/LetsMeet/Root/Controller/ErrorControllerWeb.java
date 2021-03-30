package com.LetsMeet.LetsMeet.Root.Controller;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.LetsMeet.LetsMeet.User.Model.User;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.SessionAttributes;

@Controller
@SessionAttributes("userlogin")
public class ErrorControllerWeb implements ErrorController {

    @GetMapping("/error")
    public String Error(Model model, HttpSession session, HttpServletRequest request){

        User user = (User) session.getAttribute("userlogin");
        if(!(user == null)) {
            model.addAttribute("user", user);
        }

        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);

        if(status != null){
            Integer statusCode = Integer.valueOf(status.toString());

            if (statusCode == HttpStatus.NOT_FOUND.value()) {
                // handle HTTP 404 Not Found error
                return "error/404";

            } else if (statusCode == HttpStatus.FORBIDDEN.value()) {
                // handle HTTP 403 Forbidden error
                return "error/403";

            } else if (statusCode == HttpStatus.INTERNAL_SERVER_ERROR.value()) {
                // handle HTTP 500 Internal Server error
                return "error/500";

            }
        }
        return "/error/404";
    }

    @Override
    public String getErrorPath() {
        return "/error";
    }
}

