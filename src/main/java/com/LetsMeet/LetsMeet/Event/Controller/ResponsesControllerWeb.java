package com.LetsMeet.LetsMeet.Event.Controller;

import com.LetsMeet.LetsMeet.Event.Model.DTO.ResponseDTO;
import com.LetsMeet.LetsMeet.Event.Service.EventResponseService;
import com.LetsMeet.LetsMeet.Event.Service.EventService;
import com.LetsMeet.LetsMeet.User.Model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping(("/event/{eventUUID}/respond"))
@SessionAttributes("userlogin")
public class ResponsesControllerWeb {

    @Autowired
    EventResponseService eventResponseService;

    @Autowired
    EventService eventService;

    static String RESPONSE_TEMPLATE = "event/response";
    static String GUEST_TEMPLATE = "event/guestResponsePreface";

    @GetMapping()
    public String httpResponseGet(Model model, RedirectAttributes redirectAttributes, HttpSession session,
    @PathVariable("eventUUID") String eventUUID,
    @RequestParam(name = "guest", defaultValue = "false") boolean guest){
        User user = (User) session.getAttribute("userlogin");
        if(user == null){
            return "";
        }

        try{
            ResponseDTO responseDTO = new ResponseDTO();
            model.addAttribute("response", responseDTO);
        }
        catch (Exception e){

        }



    }

    @PostMapping()
    public String httpResponseSet(Model model, RedirectAttributes redirectAttributes, HttpSession session,
    @PathVariable("eventUUID") String eventUUID,
    @RequestParam(name = "guest", defaultValue = "false") boolean guest,
    @ModelAttribute ResponseDTO responseDTO){

    }

    @GetMapping("/guest")
    public String httpResponseGuestGet(){ return "redirect:..?guest=true";}

    @PostMapping("/guest")
    public String getHttpResponseGuestSet(Model model, RedirectAttributes redirectAttributes, HttpSession session){}
}
