package com.LetsMeet.LetsMeet.Business.Controller;

import com.LetsMeet.LetsMeet.Business.Model.Business;
import com.LetsMeet.LetsMeet.Business.Service.BusinessService;
import com.LetsMeet.LetsMeet.Venue.Model.Venue;
import com.LetsMeet.LetsMeet.Venue.Service.VenueBusinessService;
import com.LetsMeet.LetsMeet.User.Model.User;
import com.LetsMeet.LetsMeet.User.Service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Controller
@SessionAttributes("userlogin")
public class BusinessControllerWeb {
    private static final Logger LOGGER= LoggerFactory.getLogger(BusinessControllerWeb.class);

    @Autowired
    BusinessService businessService;

    @Autowired
    VenueBusinessService venueBusinessService;

    @Autowired
    UserService userService;

    @GetMapping({"/createBusiness", "/business/new"})
    public String newBusiness(Model model, HttpSession session, RedirectAttributes redirectAttributes) {
        User user = (User) session.getAttribute("userlogin");

        if (user == null) {
            redirectAttributes.addFlashAttribute("danger", "You do not have permission to view this page.");
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

            List<Venue> venues = venueBusinessService.getBusinessVenues(business.getUUID().toString());
            model.addAttribute("venues", venues);

            if(venues == null) {
                model.addAttribute("venuesRegistered", false);
            }else{
                model.addAttribute("venuesRegistered", true);
            }

            model.addAttribute("BusinessUsers", businessService.businessUsers(business));

            return "Business/Business";
        }
    }

    @PostMapping({"/createBusiness", "/business/new"})
    public String saveBusiness(HttpSession session, Model model, RedirectAttributes redirectAttributes,
                               @RequestParam(value="Name") String name){
        // Validate user
        User user = (User) session.getAttribute("userlogin");
        if (user == null) {
            redirectAttributes.addFlashAttribute("accessDenied", "An error occurred when creating the Business.");
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

    @GetMapping("/Business/{BusinessID}/edit")
    public String editBusiness(HttpSession session, Model model, RedirectAttributes redirectAttributes,
                               @PathVariable(value="BusinessID") String businessUUID){
        // Get business and user
        Business business = businessService.getBusiness(businessUUID);
        User user = (User) session.getAttribute("userlogin");

        // Check user has permission
        if(!(business == null) && !(user == null) && businessService.isOwner(user, business)){
            model.addAttribute("user", user);
            model.addAttribute("business", business);
            return "Business/editBusiness";
        }

        redirectAttributes.addFlashAttribute("accessDenied", "You don't have permission to view this page");
        return "redirect:/Home";
    }
    
    @PostMapping("/Business/{BusinessID}/edit")
    public String editBusinessDetails(HttpSession session, Model model, RedirectAttributes redirectAttributes,
                                      @PathVariable(value="BusinessID") String businessUUID,
                                      @RequestParam(value="Name") String name){
        // Get user and business
        Business business = businessService.getBusiness(businessUUID);
        User user = (User) session.getAttribute("userlogin");

        // Check user has permission
        if(businessService.isOwner(user, business)) {
            // Update business
            if(businessService.updateBusiness(business, name)) {

                // Return to business page
                String destination = String.format("redirect:/Business/%s", businessUUID);
                return destination;
            }
            redirectAttributes.addFlashAttribute("accessDenied", "Something went wrong!");
            return "redirect:/Home";
        }

        redirectAttributes.addFlashAttribute("accessDenied", "You don't have permission to carry out this action");
        return "redirect:/Home";
    }

    @PostMapping("/Business/{BusinessID}/delete")
    public String deleteBusiness(HttpSession session, Model model, RedirectAttributes redirectAttributes,
                                 @PathVariable(value="BusinessID") String businessUUID){

        // Get user and business
        User user = (User) session.getAttribute("userlogin");
        Business business = businessService.getBusiness(businessUUID);

        // Check user has permission
        if(!(user == null) && !(business == null) && businessService.isOwner(user, business)){
            businessService.deleteBusiness(businessUUID, user.getUUID().toString());

            // Return to user dashboard
            redirectAttributes.addFlashAttribute("success", "Business successfully deleted");
            return "redirect:/dashboard";
        }

        redirectAttributes.addFlashAttribute("accessDenied", "You don't have permission to carry out this action");
        String destination = String.format("redirect:/Business/%s", businessUUID);
        return destination;
    }

    @PostMapping("/Business/{BusinessID}/addUsers")
    public String addUserToBusiness(HttpSession session, Model model, RedirectAttributes redirectAttributes,
                                    @PathVariable(value="BusinessID") String businessUUID,
                                    @RequestParam(value="User") String users){
        System.out.println("Business Controller : addUserToBusiness");
        System.out.println(users);

        // Get business
        Business business = businessService.getBusiness(businessUUID);

        if(business == null){
            return "redirect:/dashboard";
        }

        // Get users into a list
        if(!users.equals("")){
            List<String> usersToAdd = new ArrayList<>(Arrays.asList(users.split(",")));
            usersToAdd.removeAll(Collections.singletonList(""));
            if(usersToAdd.size() > 0){
                User user;
                for(String s : usersToAdd){
                    user = null;

                    // Attempt to get user by uuid
                    try {
                        user = userService.getUserByUUID(s);
                    }catch(Exception e){
                        // We dont care about this
                    }

                    if(user == null) {
                        // If no user, attempts to get by email
                        user = userService.getUserByEmail(s);
                    }

                    if(!(user == null)){
                        // join business
                        businessService.joinBusiness(business, user);
                    }
                }
            }
        }

        String destination = String.format("redirect:/Business/%s", business.getUUID().toString());
        return destination;
    }

    // Error catching
    @ExceptionHandler(Exception.class)
    public String handleException(){
        return "redirect:/405";
    }
}
