package com.hnau.platform.common;

/**
 * 系统状态管理类
 * 用于跟踪系统是否处于更新状态
 */
public class SystemStatusManager {
    
    // 系统是否处于更新状态
    private static boolean isUpdating = false;
    
    // 私有构造方法，防止实例化
    private SystemStatusManager() {
    }
    
    /**
     * 获取系统是否处于更新状态
     * @return 是否处于更新状态
     */
    public static boolean isUpdating() {
        return isUpdating;
    }
    
    /**
     * 设置系统更新状态
     * @param updating 是否处于更新状态
     */
    public static void setUpdating(boolean updating) {
        isUpdating = updating;
    }
}
