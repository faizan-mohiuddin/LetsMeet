package com.LetsMeet.LetsMeet.Event.Controller;

import com.LetsMeet.LetsMeet.User.Model.User;
import com.LetsMeet.LetsMeet.User.Service.UserService;
import com.LetsMeet.LetsMeet.User.Service.ValidationService;
import com.LetsMeet.LetsMeet.Event.Service.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
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

            model.addAttribute("user", user);

            return "createevent";

        }

    }

    @GetMapping("/saveevent")
    public String saveevent(@RequestParam(name = "eventname") String eventname, @RequestParam(name = "eventdesc") String eventdesc, @RequestParam(name = "eventlocation") String eventlocation, HttpSession session, Model model, RedirectAttributes redirectAttributes){

        User user = (User) session.getAttribute("userlogin");

        if (user == null){

            redirectAttributes.addFlashAttribute("accessDenied", "An error occurred when creating the event.");

            return "redirect:/Home";

        } else {

            model.addAttribute("user", user);
            model.addAttribute("eventname", eventname);
            model.addAttribute("eventdesc", eventdesc);
            model.addAttribute("eventlocation", eventlocation);


            EventServiceInterface.createEvent(eventname, eventdesc, eventlocation, user.getUUID().toString());

            return "saveevent";

        }

    }

    @GetMapping("/adminviewallevents")
    public String adminviewallevents(Model model, RedirectAttributes redirectAttributes, HttpSession session) {

        User user = (User) session.getAttribute("userlogin");

        if (user == null || !user.getUUID().toString().equals("48f9f376-0dc0-38e4-bae9-f4e50f5f73db")) { // this user UUID is the admin account's UUID

            redirectAttributes.addFlashAttribute("accessDenied", "You do not have permission to view this page.");

            return "redirect:/Home";

        } else {

            model.addAttribute("user", user);

            model.addAttribute("allevents", EventServiceInterface.getEvents());

            return "adminviewallevents";

        }
    }

    @GetMapping("/deleteevent/{eventuuid}")
    public String deleteEvent(@PathVariable("eventuuid") String eventuuid, Model model, RedirectAttributes redirectAttributes, HttpSession session) {

        User user = (User) session.getAttribute("userlogin");

        if (user == null) {

            redirectAttributes.addFlashAttribute("accessDenied", "You do not have permission to execute this action.");

            return "redirect:/Home";

        } else {

            String tryDeleteEvent = EventServiceInterface.deleteEvent(eventuuid, user);

            if (tryDeleteEvent.equals("Event successfully deleted.")) {

                redirectAttributes.addFlashAttribute("success", "The event was successfully deleted.");

            } else {

                redirectAttributes.addFlashAttribute("danger", "An error occurred when deleting the event.");

            }
            return "redirect:/dashboard";

        }

    }

}
