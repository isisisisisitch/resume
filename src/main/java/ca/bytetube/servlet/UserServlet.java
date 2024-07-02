package ca.bytetube.servlet;


import ca.bytetube.bean.User;
import ca.bytetube.bean.Website;
import ca.bytetube.beans.util.Beans;
import ca.bytetube.dao.UserDao;
import ca.bytetube.dao.impl.UserDaoImpl;
import ca.bytetube.service.AwardService;
import ca.bytetube.service.SkillService;
import ca.bytetube.service.WebsiteService;
import ca.bytetube.service.impl.AwardServiceImpl;
import ca.bytetube.service.impl.SkillServiceImpl;
import ca.bytetube.service.impl.WebsiteServiceImpl;
import ca.bytetube.util.Uploads;
import com.google.code.kaptcha.impl.DefaultKaptcha;
import com.google.code.kaptcha.util.Config;
import org.apache.commons.fileupload.FileItem;

import javax.imageio.ImageIO;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Properties;

@WebServlet("/user/*")
public class UserServlet extends BaseServlet {
    private static final UserDao service = new UserDaoImpl();
    private static final WebsiteService websiteService = new WebsiteServiceImpl();
    private static final SkillService skillService = new SkillServiceImpl();
    private static final AwardService awardService = new AwardServiceImpl();

    public void login(HttpServletRequest request, HttpServletResponse response) throws Exception {
        //1.校验验证码
        String captcha = request.getParameter("captcha").toLowerCase();
        String code = (String) request.getSession().getAttribute("code");
        if (!captcha.equals(code)) {
            forwardError("captcha is wrong", request, response);
            return;
        }

        User user = Beans.convert(request.getParameterMap(), User.class);
        user = service.find(user);
        if (user != null) {
            request.getSession().setAttribute("user", user);
            redirect("user/admin", request, response);
        } else {
            forwardError("username or password is wrong", request, response);
        }
    }

    public void editPassword(HttpServletRequest request, HttpServletResponse response) throws Exception {
        request.setAttribute("menu", "password");
        forward("admin/password.jsp", request, response);
    }

    public void updatePassword(HttpServletRequest request, HttpServletResponse response) throws Exception {
        User user = (User) request.getSession().getAttribute("user");
        String originPassword = user.getPassword();
        String oldPassword = request.getParameter("oldPassword");
        if (originPassword.equals(oldPassword)) {
            String newPassword = request.getParameter("password");
            user.setPassword(newPassword);
            if (service.save(user)) {
                logout(request, response);
            } else {
                user.setPassword(originPassword);
                forwardError("update password failed", request, response);
            }
        } else {
            forwardError("old password is wrong", request, response);
        }
    }

    public void logout(HttpServletRequest request, HttpServletResponse response) throws Exception {
        request.getSession().removeAttribute("user");
        redirect("page/login.jsp", request, response);
    }

    public void save(HttpServletRequest request, HttpServletResponse response) throws Exception {
        User loginUser = (User) request.getSession().getAttribute("user");
        Map<String, Object> params = Uploads.getParams(request);
        User user = Beans.convert(params, User.class);
        user.setPhoto(Uploads.uploadImage((FileItem) params.get("photoFile"), request, user.getPhoto()));
        user.setId(loginUser.getId());
        user.setPassword(loginUser.getPassword());
        user.setEmail(loginUser.getEmail());

        if (service.save(user)) {
            redirect("user/admin", request, response);
        } else {
            forwardError("sve user failed", request, response);
        }
    }

    public void admin(HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        User user = service.find().get(0);
        request.setAttribute("user", user);
        request.setAttribute("menu", "user");
        request.getSession().setAttribute("user", user);
        forward("admin/user.jsp", request, response);
    }

    public void remove(HttpServletRequest request, HttpServletResponse response) throws Exception {

    }

    public void captcha(HttpServletRequest request, HttpServletResponse response) throws Exception {
        //1.创建captcha对象
        DefaultKaptcha dk = new DefaultKaptcha();

        //2.设置验证码的样式配置
        try {
            Properties properties = new Properties();
            InputStream is = getClass().getClassLoader().getResourceAsStream("kaptcha.properties");
            properties.load(is);
            Config config = new Config(properties);
            dk.setConfig(config);
        } catch (Exception e) {
            e.printStackTrace();
        }

        //3.生成验证码字符串
        String code = dk.createText();
        //存储验证码到session中（当客户端第一次被访问时，服务器会创建一个全新的session）
        HttpSession session = request.getSession();
        session.setAttribute("code", code.toLowerCase());

        BufferedImage image = dk.createImage(code);

        response.setContentType("image/jpeg");
        //将图片写回前端
        ImageIO.write(image, "jpg", response.getOutputStream());


    }

    public void front(HttpServletRequest request, HttpServletResponse response) throws Exception {
        User user = service.find().get(0);
        request.setAttribute("user", user);
        request.getSession().setAttribute("user", user);
        List<Website> websites = websiteService.find();
        if (websites.size() > 0) request.setAttribute("website", websites.get(0));
        request.setAttribute("skills", skillService.find());
        request.setAttribute("awards", awardService.find());

        forward("front/user.jsp", request, response);
    }
}
