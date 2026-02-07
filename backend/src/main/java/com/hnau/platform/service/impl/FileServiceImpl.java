package com.hnau.platform.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hnau.platform.entity.File;
import com.hnau.platform.mapper.FileMapper;
import com.hnau.platform.service.FileService;
import org.springframework.stereotype.Service;

/**
 * 文件服务实现类
 */
@Service
public class FileServiceImpl extends ServiceImpl<FileMapper, File> implements FileService {
}