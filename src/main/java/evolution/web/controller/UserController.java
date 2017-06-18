package evolution.web.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import evolution.dao.FeedRepository;
import evolution.dao.FriendsDao;
import evolution.dao.UserDao;
import evolution.model.friend.Friends;
import evolution.model.user.User;
import evolution.service.MyJacksonService;
import evolution.service.SearchService;
import evolution.service.builder.JsonInformationBuilder;
import evolution.service.security.UserDetailsServiceImpl;
import evolution.service.validation.Validator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;

import javax.persistence.NoResultException;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;

/**
 * Created by Admin on 05.03.2017.
 */
@Controller
@RequestMapping ("/user")
@SessionAttributes(value = {"user"})
public class UserController {

    @Autowired
    private JsonInformationBuilder jsonInformationBuilder;
    @Autowired
    private UserDao userDao;
    @Autowired
    private MyJacksonService jacksonService;
    @Autowired
    private FriendsDao friendsDao;
    @Autowired
    private Validator validator;
    @Autowired
    private SearchService searchService;
    @Autowired
    private FeedRepository feedRepository;
    private Logger LOGGER = LoggerFactory.getLogger(UserController.class);

    @RequestMapping (value = "/id{id}", method = RequestMethod.GET)
    public String home (
            @PathVariable Long id,
            Model model,
            @SessionAttribute(required = false) User authUser) {

        if (authUser.getId().equals(id)) {
            model.addAttribute("user", authUser);
            LOGGER.info("session user\n" + authUser);
            LOGGER.info("My home. User id = " + id);
        } else {
            try {
                Friends friends = friendsDao.findUserAndFriendStatus(authUser.getId(), id);
                model.addAttribute("user", friends.getUser());
                LOGGER.info("session user\n" + friends.getUser());
                model.addAttribute("status", friends.getStatus());
                LOGGER.info("Other user id = " + friends.getUser().getId());
            } catch (NoResultException e) {
                LOGGER.error("User by id " + id +", is not exist\n" + e);
                return "redirect:/user/id" + authUser.getId();
            }
        }

//        model.addAttribute("feed", feedRepository.feed(id, 100, 0));

        return "user/my-home";
    }

    @ResponseBody @RequestMapping(value = "/", method = RequestMethod.GET,
            produces={"application/json; charset=UTF-8"})
    public String allUser(@RequestParam Integer limit,
                          @RequestParam Integer offset) throws JsonProcessingException {

        List list = userDao.findAll(limit, offset);
        return jacksonService.objectToJson(list);
    }

    // EDIT
    @ResponseBody @RequestMapping(value = "/{id}", method = RequestMethod.PUT,
            produces={"application/json; charset=UTF-8"})
    public String edit(@RequestBody String json,
                        @PathVariable Long id,
                        @SessionAttribute User user,
                        @AuthenticationPrincipal UserDetailsServiceImpl.CustomUser customUser,
                        HttpServletRequest request) throws IOException {

        User userRequest = (User) jacksonService.jsonToObject(json, User.class);
        // self update
        if (customUser.getUser().getId().equals(id)) {
            userRequest.setId(customUser.getUser().getId());
            userRequest.setLogin(customUser.getUser().getLogin());
            userRequest.setRegistrationDate(customUser.getUser().getRegistrationDate());
            userRequest.setRoleId(customUser.getUser().getRoleId());
        }
        //other update
        else if (request.isUserInRole("ROLE_ADMIN")) {
            userRequest.setId(user.getId());
            userRequest.setRegistrationDate(user.getRegistrationDate());

            // in future change login
            userRequest.setLogin(user.getLogin());
        }

        if (validator.userValidator(userRequest)) {
            userDao.repository().update(userRequest);
            if (customUser.getUser().getId().equals(id))
                customUser.getUser().updateFields(userRequest);
            return jsonInformationBuilder.buildJson(HttpStatus.OK.toString(), null, true);
        }

        return jsonInformationBuilder.buildJson(HttpStatus.OK.toString(), null, false);
    }

    // DELETE
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @ResponseBody @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public void deleteUser(@PathVariable Long id) {
        userDao.repository().delete(new User(id));
    }

    // GET FORM PROFILE
    @RequestMapping(value = "/profile/{id}", method = RequestMethod.GET)
    public String profile(@PathVariable Long id,
                          @AuthenticationPrincipal UserDetailsServiceImpl.CustomUser customUser,
                          HttpServletRequest request,
                          Model model) {

        LOGGER.info("session user\n" + request.getSession().getAttribute("user"));


        if (id.equals(customUser.getUser().getId())) {
            model.addAttribute("user", customUser.getUser());
            return "user/form-my-profile";
        } else if (request.isUserInRole("ROLE_ADMIN")) {
            model.addAttribute("user", userDao.find(id));
            return "admin/admin-form-profile";
        }

        return "redirect:/user/id" + customUser.getUser().getId();
    }

    @RequestMapping(value = "/search", method = RequestMethod.GET)
    public String viewSearch(Model model, SessionStatus sessionStatus){
        sessionStatus.setComplete();
        LOGGER.info("session status set complete");
        int limit = 5;
        model.addAttribute("limit", limit);
        model.addAttribute("list", userDao.findAll(limit, 0));
        return "user/new-search";
    }

    @ResponseBody @RequestMapping(value = "/search-result", method = RequestMethod.GET, produces={"application/json; charset=UTF-8"})
    public String resultSearch(@RequestParam String like,
                               @RequestParam Integer limit,
                               @RequestParam Integer offset) throws JsonProcessingException {
        return jacksonService.objectToJson(searchService.searchUser(like, limit, offset));
    }

    @RequestMapping(value = "/ex", method = RequestMethod.GET)
    public String ex() {
        throw new NullPointerException();
    }
}