package cn.nome.saas.allocation.repository.entity.allocation;

/**
 * WarehouseRecordDO
 *
 * @author Bruce01.fan
 * @date 2019/9/9
 */
public class WarehouseRecordDO {

    private int id;

    private int taskId;

    private int configId;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getTaskId() {
        return taskId;
    }

    public void setTaskId(int taskId) {
        this.taskId = taskId;
    }

    public int getConfigId() {
        return configId;
    }

    public void setConfigId(int configId) {
        this.configId = configId;
    }
}
