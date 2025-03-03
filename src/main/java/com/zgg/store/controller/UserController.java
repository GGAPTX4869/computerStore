package com.zgg.store.controller;

import com.zgg.store.controller.ex.*;
import com.zgg.store.entity.User;
import com.zgg.store.service.UserService;
import com.zgg.store.util.JsonResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/users")
public class UserController extends BaseController{//继承基类
    @Autowired
    private UserService userService;

    @RequestMapping("/reg")
    public JsonResult<Void> reg(User user){
        userService.reg(user);
        return new JsonResult<>(OK);
    }

    @RequestMapping("/login")
    public JsonResult<User> login(String username, String password, HttpSession session){
        User data = userService.login(username, password);
        session.setAttribute("uid",data.getUid());
        session.setAttribute("username",data.getUsername());
        System.out.println(getuidFromSession(session));
        System.out.println(getUsernameFromSession(session));
        return new JsonResult<User>(OK,data);

    }

    @RequestMapping("/change_password")
    public JsonResult<Void> changePassword(String oldPassword,String newPassword,HttpSession session){
        Integer uid = getuidFromSession(session);
        String username = getUsernameFromSession(session);
        userService.changePassword(uid,username,oldPassword,newPassword);
        return new JsonResult<Void>(OK);
    }
    @RequestMapping("/get_by_uid")
    public JsonResult<User> getByUid(HttpSession session){
        User data = userService.getByUid(getuidFromSession(session));
        return new JsonResult<User>(OK,data);
    }

    @RequestMapping("/change_info")
    public JsonResult<Void> changeInfo(User user,HttpSession session){
        //user对象有四部分的数据：username、phone,email,gender
        //uid数据需要再次封装到user对象中
        userService.changeInfo(getuidFromSession(session),getUsernameFromSession(session),user);

        return new JsonResult<Void>(OK);
    }


    //设置上传文件的最大值
    public static final int AVATAR_MAX_SIZE=10*1024*1024;
    //限制上传文件的类型
    public static final List<String> AVATAR_TYPE=new ArrayList<>();
    static {
        AVATAR_TYPE.add("image/jpeg");
        AVATAR_TYPE.add("image/png");
        AVATAR_TYPE.add("image/bmp");
        AVATAR_TYPE.add("image/gif");
    }

    /**
     * 修改用户的头像
     * MultipartFile接口是SpringMVC提供的一个接口，这个接口为我们
     * 包装了获取文件类型的数据（任何类型的file都可以接收），SpringBoot
     * 它有整合了SpringMVC，只需要在处理请求的方法参数列表上声明一个参数
     * 类型为MultipartFile的参数，然后SpringBoot自动将传递给服务的文件
     * 数据赋值给这个参数
     *
     * @RequestParam 表示请求中的参数，将请求中的参数注入请求处理方法的
     * 某个参数上，如果名称不一致则可以使用@RequestParam注解进行标记和映射
     *
     * @param session
     * @param file
     * @return
     */
    @RequestMapping("/change_avatar")
    public JsonResult<String> changeAvatar(
            HttpSession session,
            @RequestParam("file") MultipartFile file) throws FileNotFoundException {
    //判断文件是否为null
        if(file.isEmpty()){
            throw new FileEmptyExcepetion("文件为空");
        }
        //判断文件大小
        if(file.getSize()>AVATAR_MAX_SIZE){
            throw new FileSizeException("文件超出限制");
        }
        //判断文件类型
        String contentType = file.getContentType();
        //如果集合包含某个元素返回true，这里取反
        if(!AVATAR_TYPE.contains(contentType)){
            throw new FileTypeException("文件类型不支持");
        }
        //上传的文件.../upload/文件.png
//        String parent=session.getServletContext().getRealPath("/upload");
//        String parent= ResourceUtils.getURL("classpath:").getPath()+"static/upload/";
        String parent=System.getProperty("user.dir")+"\\src\\main\\resources\\static\\images\\upload";
        System.out.println(parent);
        //File对象指向这个路径，File是否存在
        File dir = new File(parent);
        if(!dir.exists()){//检测目录是否存在
            dir.mkdirs();//创建当前目录
        }
        //获取到这个文件的名称，uuid工具类来生成一个新的字符串作为文件名
        //例如：avatao01.png
        String originalFilename = file.getOriginalFilename();
        int index = originalFilename.lastIndexOf(".");
        String suffix = originalFilename.substring(index);
        String s = UUID.randomUUID().toString().toUpperCase();
        String filename= s+suffix;

        //创建一个空文件
        File dest = new File(dir, filename);
        //参数file中数据写入到这个空文件中
        try {
            file.transferTo(dest);//将file文件中的数据写入到dest文件中
        } catch (IOException e) {
            throw new FileUploadIOException("文件读写异常");
        }catch (FileStateException e){
            throw new FileStateException("文件状态异常");
        }
        //返回头像的路径/upload/test.png
        String avatar="/static/images/upload/"+filename;
        System.out.println(avatar);
        userService.changeAvatar(getuidFromSession(session),avatar,getUsernameFromSession(session));
        //返回用户头像的路径给前端页面，将来用于头像展示使用
        return new JsonResult<String>(OK,avatar);
    }

}
