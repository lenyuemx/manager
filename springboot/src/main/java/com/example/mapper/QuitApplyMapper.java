package com.example.mapper;

import com.example.entity.QuitApply;
import org.apache.ibatis.annotations.Param;
import java.util.List;

public interface QuitApplyMapper {

    void insert(QuitApply quitApply);

    void deleteById(Integer id);

    void updateById(QuitApply quitApply);

    QuitApply selectById(Integer id);

    List<QuitApply> selectAll(QuitApply quitApply);
    
}
