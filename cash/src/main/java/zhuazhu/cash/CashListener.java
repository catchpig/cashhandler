package zhuazhu.cash;

/**
 * 创建时间:2018-03-12 22:01<br/>
 * 创建人: 李涛<br/>
 * 修改人: 李涛<br/>
 * 修改时间: 2018-03-12 22:01<br/>
 * 描述:
 */

public interface CashListener {
    /**
     * 返回异常日志路径
     * @param fileName
     */
    void cashFilePath(String fileName);
}
