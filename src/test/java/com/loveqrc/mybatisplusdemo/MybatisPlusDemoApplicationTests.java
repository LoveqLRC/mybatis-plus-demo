package com.loveqrc.mybatisplusdemo;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.loveqrc.mybatisplusdemo.entity.User;
import com.loveqrc.mybatisplusdemo.mapper.UserMapper;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class MybatisPlusDemoApplicationTests {

    @Autowired
    private UserMapper userMapper;

    //    User(id=1, name=Jone, age=18, email=test1@baomidou.com)
//    User(id=2, name=Jack, age=20, email=test2@baomidou.com)
//    User(id=3, name=Tom, age=28, email=test3@baomidou.com)
//    User(id=4, name=Sandy, age=21, email=test4@baomidou.com)
//    User(id=5, name=Billie, age=24, email=test5@baomidou.com)
    @Test
    public void contextLoads() {
        List<User> userList = userMapper.selectList(null);
        Assert.assertEquals(5, userList.size());
        userList.forEach(System.out::println);

    }

    //名字中包含J并且年龄小于20
    // name like '%J%' and age <20;
    @Test
    public void selectByWrapper() {
        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        userQueryWrapper.like("name", "J")
                .lt("age", 20);
        List<User> userList = userMapper.selectList(userQueryWrapper);
        userList.forEach(System.out::println);
    }

    //名字中包含J并且年龄大于等于20且小于等于40并且email不为空
    //name like '%J%'and age between 20 and 40 and email is not null
    @Test
    public void selectByWrapper2() {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.like("name", "J")
                .between("age", 20, 40)
                .isNotNull("email");
        List<User> userList = userMapper.selectList(queryWrapper);
        userList.forEach(System.out::println);
    }

    //名字为J姓或者年龄大于25，按照年龄降序排列，年龄相同按照ID升序排列
    // name like 'J%' or age >=25 order by age desc,id asc
    @Test
    public void selectByWrapper3() {
        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        userQueryWrapper.likeRight("name", 'J')
                .or().gt("age", 25)
                .orderByDesc("age")
                .orderByAsc("id");
        List<User> users = userMapper.selectList(userQueryWrapper);
        users.forEach(System.out::println);
    }


    @Test
    public void selectByWrapper4() {

    }
}
