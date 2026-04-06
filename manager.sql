

-- ----------------------------
-- Table structure for new_department_apply
-- ----------------------------
DROP TABLE IF EXISTS `new_department_apply`;
CREATE TABLE `new_department_apply` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `name` varchar(255) NOT NULL COMMENT 'ç¤¾å›¢åç§°',
  `description` text COMMENT 'ç¤¾å›¢æè¿°',
  `applicant_id` int(11) NOT NULL COMMENT 'ç”³è¯·äººID',
  `teacher_id` int(11) DEFAULT NULL COMMENT 'æŒ‡å¯¼è€å¸ˆID',
  `apply_time` varchar(255) DEFAULT NULL COMMENT 'ç”³è¯·æ—¶é—´',
  `approval_description` text COMMENT 'å®¡æ‰¹è¯´æ˜',
  `status` varchar(255) NOT NULL DEFAULT 'å¾…å®¡æ‰¹' COMMENT 'çŠ¶æ€ï¼šå¾…å®¡æ‰¹ã€å·²æ‰¹å‡†ã€å·²é©³å›',
  `cover` varchar(255) DEFAULT NULL COMMENT 'ç¤¾å›¢å›¾æ ‡',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='æ–°å»ºç¤¾å›¢ç”³è¯·è¡¨';

-- ----------------------------
-- Table structure for likes
-- ----------------------------
DROP TABLE IF EXISTS `likes`;
CREATE TABLE `likes` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `user_id` int(11) NOT NULL COMMENT 'ç‚¹èµäººID',
  `foreign_id` int(11) NOT NULL COMMENT 'å…³è”æ–‡ç« ID',
  `module` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'å½’å±æ¨¡å—',
  `time` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT 'ç‚¹èµæ—¶é—´',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='ç‚¹èµè¡¨';

-- ²¹³ä¸üĞÂ£ºÎª department ±íÔö¼Ó teacher_id ×Ö¶Î
ALTER TABLE association_manager.department ADD COLUMN teacher_id INT DEFAULT NULL COMMENT 'Ö¸µ¼ÀÏÊ¦ID' AFTER user_id;

-- ----------------------------
-- Table structure for quit_apply
-- ----------------------------
DROP TABLE IF EXISTS `quit_apply`;
CREATE TABLE `quit_apply` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `user_id` int(11) NOT NULL COMMENT 'ÉêÇëÍËÍÅÈËID',
  `department_id` int(11) NOT NULL COMMENT 'ÍË³öµÄÉçÍÅID',
  `reason` text COMMENT 'ÍËÍÅÀíÓÉ',
  `apply_time` varchar(255) DEFAULT NULL COMMENT 'Ìá½»Ê±¼ä',
  `status` varchar(255) NOT NULL DEFAULT '´ıÉóÅú' COMMENT '×´Ì¬£º´ıÉóÅú¡¢ÒÑÅú×¼¡¢ÒÑ²µ»Ø',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='ÍËÍÅÉêÇë±í';


ALTER TABLE activity ADD COLUMN status VARCHAR(255) DEFAULT 'Í¨¹ı';
ALTER TABLE information ADD COLUMN status VARCHAR(255) DEFAULT 'Í¨¹ı';
