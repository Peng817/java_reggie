package com.pengyan.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pengyan.reggie.entity.AddressBook;
import com.pengyan.reggie.mapper.AddressBookMapper;
import com.pengyan.reggie.service.AddressBookService;
import org.springframework.stereotype.Service;

/**
 * @author 彭琰
 */
@Service
public class AddressBookServiceImpl extends ServiceImpl<AddressBookMapper, AddressBook> implements AddressBookService {
}
