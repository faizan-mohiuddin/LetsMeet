package com.LetsMeet.LetsMeet.Business.Controller;

import com.LetsMeet.LetsMeet.Business.Model.Business;
import com.LetsMeet.LetsMeet.Business.Service.BusinessService;
import com.LetsMeet.LetsMeet.Event.Controller.EventControllerWeb;
import com.LetsMeet.LetsMeet.Event.Model.Event;
import com.LetsMeet.LetsMeet.Root.Media.Media;
import com.LetsMeet.LetsMeet.User.Model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;

@Controller
@SessionAttributes("userlogin")
public class BusinessControllerWeb {
    private static final Logger LOGGER= LoggerFactory.getLogger(BusinessControllerWeb.class);

    @Autowired
    BusinessService businessService;


    @GetMapping({"/createBusiness", "/business/new"})
    public String newEvent(Model model, HttpSession session, RedirectAttributes redirectAttributes) {
        User user = (User) session.getAttribute("userlogin");

        if (user == null) {
            redirectAttributes.addFlashAttribute("accessDenied", "You do not have permission to view this page.");
            return "redirect:/Home";
        }

        else {
            model.addAttribute("user", user);
            return "Business/createBusiness";
        }
    }

    @GetMapping("Business/{ID}")
    public String getBusiness(HttpSession session, Model model, RedirectAttributes redirectAttributes,
                              @PathVariable(value="ID") String uuid){
        // Validate user
        User user = (User) session.getAttribute("userlogin");
        if (user == null) {
            redirectAttributes.addFlashAttribute("accessDenied", "You don't have permission to view this page");
            return "redirect:/Home";
        }else{
            // Get business
            Business business = businessService.getBusiness(uuid);
            // Check user has permission to view page
            if(!businessService.isOwner(user, business)){
                redirectAttributes.addFlashAttribute("accessDenied", "You don't have permission to view this page");
                return "redirect:/Home";
            }

            // Display business page
            model.addAttribute("user", user);
            model.addAttribute("business", business);

            return "Business/Business";
        }
    }

    @PostMapping({"/createBusiness", "/business/new"})
    public String saveBusiness(HttpSession session, Model model, RedirectAttributes redirectAttributes,
                               @RequestParam(value="Name") String name){
        // Validate user
        User user = (User) session.getAttribute("userlogin");
        if (user == null) {
            redirectAttributes.addFlashAttribute("accessDenied", "An error occurred when creating the event.");
            return "redirect:/Home";
        }

        try{

            model.addAttribute("user", user);
            model.addAttribute("businessName", name);

            System.out.println("Business name");
            System.out.println(name);

            Business b = businessService.createBusiness(name, user);
            String redirectAddress = String.format("redirect:/Business/%s", b.getUUID().toString());
            return redirectAddress;
        }

        catch(Exception e){
            redirectAttributes.addFlashAttribute("accessDenied", "Creation failed");
            return "redirect:/Home";
        }
    }
}
