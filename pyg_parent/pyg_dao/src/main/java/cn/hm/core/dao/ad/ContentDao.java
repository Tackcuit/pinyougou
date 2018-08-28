package cn.hm.core.dao.ad;

import cn.hm.core.pojo.ad.Content;
import cn.hm.core.pojo.ad.ContentQuery;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface ContentDao {
    int countByExample(ContentQuery example);

    int deleteByExample(ContentQuery example);

    int deleteByPrimaryKey(Long id);

    int insert(Content record);

    int insertSelective(Content record);

    List<Content> selectByExample(ContentQuery example);

    Content selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") Content record, @Param("example") ContentQuery example);

    int updateByExample(@Param("record") Content record, @Param("example") ContentQuery example);

    int updateByPrimaryKeySelective(Content record);

    int updateByPrimaryKey(Content record);
}