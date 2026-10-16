-- 视频表新增封面图字段 + 把「剑与花的远征 · 同人动画 PV」换成「超时空辉夜姬」
ALTER TABLE video
    ADD COLUMN cover_url VARCHAR(255) DEFAULT NULL COMMENT '封面图 URL（优先于 grad 渐变）' AFTER grad;

UPDATE video
SET title     = '超时空辉夜姬',
    cover_url = '/kaguya.webp'
WHERE id = 12;

SELECT id, title, cover_url FROM video WHERE id = 12;
