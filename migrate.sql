ALTER TABLE `activity` ADD COLUMN `status` VARCHAR(255) DEFAULT '通过' COMMENT '状态：待审核、通过、驳回';
ALTER TABLE `information` ADD COLUMN `status` VARCHAR(255) DEFAULT '通过' COMMENT '状态：待审核、通过、驳回';
