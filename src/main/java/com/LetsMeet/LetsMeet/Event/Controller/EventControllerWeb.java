package com.LetsMeet.LetsMeet.Event.Controller;

import com.LetsMeet.LetsMeet.User.Model.User;
import com.LetsMeet.LetsMeet.User.Service.UserService;
import com.LetsMeet.LetsMeet.User.Service.ValidationService;
import com.LetsMeet.LetsMeet.Event.Service.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;

@Controller
@SessionAttributes("userlogin")
public class EventControllerWeb {

    @Autowired
    EventService EventServiceInterface;

    @Autowired
    UserService UserServiceInterface;

    @GetMapping("/createevent")
    public String createevent(Model model, HttpSession session, RedirectAttributes redirectAttributes) {

        User user = (User) session.getAttribute("userlogin");

        if (user == null) {

            redirectAttributes.addFlashAttribute("accessDenied", "You do not have permission to view this page.");

            return "redirect:/Home";

        } else {

            return "createevent";

        }

    }

    @GetMapping("/saveevent")
    public String saveevent(@RequestParam(name = "eventname") String eventname, @RequestParam(name = "eventdesc") String eventdesc, @RequestParam(name = "eventlocation") String eventlocation, HttpSession session, Model model){

        model.addAttribute("eventname", eventname);
        model.addAttribute("eventdesc", eventdesc);
        model.addAttribute("eventlocation", eventlocation);

        User user = (User) session.getAttribute("userlogin");

        EventServiceInterface.createEvent(eventname, eventdesc, eventlocation, user.getUUID().toString());

        return "saveevent";

    }

    @GetMapping("/adminviewallevents")
    public String adminviewallevents(Model model, RedirectAttributes redirectAttributes, HttpSession session) {

        User user = (User) session.getAttribute("userlogin");

        if (user == null || !user.getUUID().toString().equals("48f9f376-0dc0-38e4-bae9-f4e50f5f73db")) { // this user UUID is the admin account's UUID

            redirectAttributes.addFlashAttribute("accessDenied", "You do not have permission to view this page.");

            return "redirect:/Home";

        } else {

            model.addAttribute("events", EventServiceInterface.getEvents());

            return "adminviewallevents";

        }
    }

}
