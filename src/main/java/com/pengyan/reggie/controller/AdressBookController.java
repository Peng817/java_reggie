package com.pengyan.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.pengyan.reggie.common.BaseContext;
import com.pengyan.reggie.common.R;
import com.pengyan.reggie.entity.AddressBook;
import com.pengyan.reggie.service.AddressBookService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/addressBook")
public class AdressBookController {
    @Autowired
    private AddressBookService addressBookService;

    /**
     * 新增地址
     * @param addressBook
     * @return
     */
    @PostMapping
    public R<String> save(@RequestBody AddressBook addressBook){
        log.info("adressBook=>"+ addressBook.toString());
        addressBook.setUserId(BaseContext.getCurrentId());
        addressBookService.save(addressBook);
        return R.success("存储地址成功");
    }

    /**
     * 更新地址
     * @param addressBook
     * @return
     */
    @PutMapping
    public R<String> update(@RequestBody AddressBook addressBook){
        log.info("adressBook=>"+ addressBook.toString());
        addressBookService.updateById(addressBook);
        return R.success("更新地址成功");
    }

    /**
     * 获取地址簿
     * @return
     */
    @GetMapping("/list")
    public R<List<AddressBook>> list(){
        Long currentId = BaseContext.getCurrentId();

        LambdaQueryWrapper<AddressBook> query = new LambdaQueryWrapper<>();
        query.eq(AddressBook::getUserId,currentId);
        query.orderByDesc(AddressBook::getUpdateTime);
        List<AddressBook> addressBookList = addressBookService.list(query);

        return R.success(addressBookList);
    }

    /**
     * 根据id查找数据
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<AddressBook> getByUserID(@PathVariable Long id) {
        AddressBook address = addressBookService.getById(id);
        if(address == null){
            return R.error("没有找到该地址");
        }
        //log.info("Address:{}", address);
        return R.success(address);
    }

    /**
     * 设置默认地址
     * @param addressBook
     * @return
     */
    @PutMapping("/default")
    public R<String> setDefault(@RequestBody AddressBook addressBook){
        LambdaUpdateWrapper<AddressBook> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(AddressBook::getUserId,BaseContext.getCurrentId());
        updateWrapper.set(AddressBook::getIsDefault,0);
        addressBookService.update(updateWrapper);

        addressBook.setIsDefault(1);
        addressBookService.updateById(addressBook);
        return R.success("设置默认地址成功");
    }

    /**
     * 获取当前用户的默认地址
     * @return
     */
    @GetMapping("/default")
    public R<AddressBook> getDefault(){
        LambdaQueryWrapper<AddressBook> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AddressBook::getUserId,BaseContext.getCurrentId());
        queryWrapper.eq(AddressBook::getIsDefault,1);
        AddressBook addressBook = addressBookService.getOne(queryWrapper);
        if(addressBook == null){
            return R.error("未找到默认地址");
        }
        return R.success(addressBook);
    }

    @DeleteMapping
    public R<String> delete(@RequestParam List<Long> ids){
        addressBookService.removeByIds(ids);
        return R.success("删除成功");
    }
}
