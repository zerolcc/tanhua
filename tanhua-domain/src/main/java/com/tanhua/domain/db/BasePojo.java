package com.tanhua.domain.db;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import java.io.Serializable;
import java.util.Date;
/**
 * 抽取BasePojo
 */
@Data
public abstract class BasePojo implements Serializable {
    // fill自动填充，什么时候出发 INSERT时
    @TableField(fill = FieldFill.INSERT)
    private Date created;
    // fill自动填充，什么时候出发 INSERT_UPDATE时
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updated;
}