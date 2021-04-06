package com.LetsMeet.LetsMeet.Event.Controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import com.LetsMeet.LetsMeet.Event.Model.DTO.EventDTO;
import com.LetsMeet.LetsMeet.Event.Service.EventService;
import com.LetsMeet.LetsMeet.Root.Media.MediaService;
import com.LetsMeet.LetsMeet.User.Model.User;
import com.LetsMeet.LetsMeet.User.Service.UserService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

@Controller
@RequestMapping("/v2")
@SessionAttributes("userlogin")
public class EventsControllerWeb {

    private static final Logger LOGGER=LoggerFactory.getLogger(EventControllerWeb.class);

    @Autowired
    private UserService userService;

    @Autowired
    private EventService eventService;

    @Autowired 
    private MediaService mediaService;

    private static String EVENT_NEW = "event/new";
    private static String EVENT = "event/event";
    private static String EVENT_EDIT = "";

    private static String USER_ATTR = "user";

    /**
     * Serves a page for creating new events
     * @param model
     * @param session
     * @param redirectAttributes
     * @return
     */
    @GetMapping({"/createevent", "/event/new"})
    public ModelAndView httpEventNewGet(HttpServletRequest request, Model model, HttpSession session, RedirectAttributes redirectAttributes){
        try{
            User user = validateSession(session);
            model.addAttribute(USER_ATTR, user);

            return new ModelAndView(EVENT_NEW, model.asMap(), HttpStatus.OK);
        }
        catch (Exception e){
            LOGGER.error("Could not create Event: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("accessDenied", "An error ocurred");

            return new ModelAndView("redirect:/Home", model.asMap(), HttpStatus.FORBIDDEN);
        }
    }

    @PostMapping({"/createevent", "/event/new"})
    public ModelAndView httpEventNewPost(HttpSession session, Model model, RedirectAttributes redirectAttributes,
        @ModelAttribute EventDTO eventDTO){
            LOGGER.info(eventDTO.getName());
            return new ModelAndView("redirect:/Home", model.asMap(), HttpStatus.OK);
        }


    @PostMapping("/event/{EventUUID}/edit")
    public ModelAndView httpEventEditPost(HttpSession session, Model model, RedirectAttributes redirectAttributes,
        @PathVariable("EventUUID") String eventUUID,
        @RequestParam("file") MultipartFile file,
        @RequestParam(name = "eventname") String eventname,
        @RequestParam(name = "eventdesc") String eventdesc,
        @RequestParam(name = "eventlocation") String eventlocation,
        @RequestParam(name="jsonTimes") String tRanges){

        try{
            return new ModelAndView("redirect:/Home", model.asMap(), HttpStatus.FORBIDDEN);

        } catch(Exception e){
            LOGGER.error("Could not create Event: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("accessDenied", "An error occurred");

            return new ModelAndView("redirect:/Home", model.asMap(), HttpStatus.FORBIDDEN);
        }
        

    }




    private User validateSession(HttpSession session) throws IllegalAccessException{
        User user = (User) session.getAttribute("userlogin");
        if (user == null) 
            throw new IllegalArgumentException("Permission Denied");
        else return user;
    }

}
