package cc.hicore.qtool.XPWork.QQProxy;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;

import cc.hicore.HookItem;
import cc.hicore.ReflectUtils.MClass;
import cc.hicore.ReflectUtils.MField;
import cc.hicore.ReflectUtils.MMethod;
import cc.hicore.ReflectUtils.XPBridge;
import cc.hicore.qtool.JavaPlugin.Controller.PluginMessageProcessor;
import cc.hicore.qtool.QQManager.QQEnvUtils;
import cc.hicore.qtool.QQMessage.QQMessageUtils;
import cc.hicore.qtool.XposedInit.HostInfo;
import cc.hicore.qtool.XposedInit.ItemLoader.BaseHookItem;

@HookItem(isRunInAllProc = false, isDelayInit = false)
public class BaseRevokeProxy extends BaseHookItem {
    private static final String TAG = "BaseRevokeProxy";

    @Override
    public String getTag() {
        return TAG;
    }

    @Override
    public boolean startHook() {
        Method[] m = getMethod();
        XPBridge.HookBefore(m[0], param -> {
            ArrayList msgList = (ArrayList) param.args[0];
            if (msgList == null || msgList.isEmpty()) return;

            String GroupUin = (String) Table_RevokeInfo_Field.GroupUin().get(msgList.get(0));
            String OpUin = (String) Table_RevokeInfo_Field.OpUin().get(msgList.get(0));
            String sender = (String) Table_RevokeInfo_Field.Sender().get(msgList.get(0));
            int istroop = (int) Table_RevokeInfo_Field.IsTroop().get(msgList.get(0));
            long shmsgseq = (long) Table_RevokeInfo_Field.shmsgseq().get(msgList.get(0));
            String FriendUin;
            if (istroop == 1) {
                FriendUin = GroupUin;
            } else if (istroop == 0) {
                if (OpUin.equals(QQEnvUtils.getCurrentUin())) {
                    FriendUin = GroupUin;
                } else {
                    FriendUin = OpUin;
                }
            } else {
                if (OpUin.equals(QQEnvUtils.getCurrentUin())) {
                    FriendUin = GroupUin;
                } else {
                    FriendUin = OpUin;
                }

            }
            Object RevokeMsg = QQMessageUtils.GetMessageByTimeSeq(FriendUin, istroop, shmsgseq);
            if (RevokeMsg != null) {
                PluginMessageProcessor.submit(() -> PluginMessageProcessor.onRevoke(RevokeMsg, OpUin));
            }
        });

        XPBridge.HookBefore(m[1], param -> {
            ArrayList msgList = (ArrayList) param.args[0];
            if (msgList == null || msgList.isEmpty()) return;

            String GroupUin = (String) Table_RevokeInfo_Field.GroupUin().get(msgList.get(0));
            String OpUin = (String) Table_RevokeInfo_Field.OpUin().get(msgList.get(0));
            String sender = (String) Table_RevokeInfo_Field.Sender().get(msgList.get(0));
            int istroop = (int) Table_RevokeInfo_Field.IsTroop().get(msgList.get(0));
            long shmsgseq = (long) Table_RevokeInfo_Field.shmsgseq().get(msgList.get(0));


            String FriendUin;
            if (istroop == 1 || istroop == 0) {
                FriendUin = GroupUin;
            } else {
                FriendUin = sender;
            }
            Object RevokeMsg = QQMessageUtils.GetMessageByTimeSeq(FriendUin, istroop, shmsgseq);

            if (RevokeMsg != null) {
                PluginMessageProcessor.submit(() -> PluginMessageProcessor.onRevoke(RevokeMsg, OpUin));
            }

        });

        return true;
    }

    @Override
    public boolean isEnable() {
        return true;
    }

    @Override
    public boolean check() {
        Method[] m = getMethod();
        return m[0] != null && m[1] != null;
    }

    public Method[] getMethod() {
        Method[] m = new Method[2];
        m[0] = MMethod.FindMethod(MClass.loadClass("com.tencent.imcore.message.QQMessageFacade"), null, void.class, new Class[]{
                ArrayList.class, boolean.class
        });
        m[1] = MMethod.FindMethod(MClass.loadClass("com.tencent.imcore.message.BaseMessageManager"), null, void.class, new Class[]{
                ArrayList.class
        });
        return m;

    }

    @HookItem(isRunInAllProc = false, isDelayInit = false)
    public static class Table_RevokeInfo_Field extends BaseHookItem {
        public static Class RevokeMsgInfo() {
            return MClass.loadClass("com.tencent.mobileqq.revokemsg.RevokeMsgInfo");
        }

        public static Field GroupUin() {
            Field f = HostInfo.getVerCode() < 5670 ? MField.FindField(RevokeMsgInfo(), "a", String.class) :
                    HostInfo.getVerCode() < 8000 ? MField.FindField(RevokeMsgInfo(), "c", String.class) :
                            MField.FindField(RevokeMsgInfo(), "g", String.class);
            if (f != null) f.setAccessible(true);
            return f;
        }

        public static Field OpUin() {
            Field f = HostInfo.getVerCode() < 5670 ? MField.FindField(RevokeMsgInfo(), "b", String.class) :
                    HostInfo.getVerCode() < 8000 ? MField.FindField(RevokeMsgInfo(), "d", String.class):
                            MField.FindField(RevokeMsgInfo(),"h",String.class);
            if (f != null) f.setAccessible(true);
            return f;
        }

        public static Field Sender() {
            Field f = HostInfo.getVerCode() < 5670 ? MField.FindField(RevokeMsgInfo(), "d", String.class) :
                    HostInfo.getVerCode() < 8000 ? MField.FindField(RevokeMsgInfo(), "h", String.class):
                            MField.FindField(RevokeMsgInfo(),"n",String.class);
            if (f != null) f.setAccessible(true);
            return f;
        }

        public static Field IsTroop() {
            Field f = HostInfo.getVerCode() < 8000 ? MField.FindField(RevokeMsgInfo(), "a", int.class):
                    MField.FindField(RevokeMsgInfo(),"e",int.class);;
            if (f != null) f.setAccessible(true);
            return f;
        }

        public static Field shmsgseq() {
            Field f = HostInfo.getVerCode() < 5670 ? MField.FindField(RevokeMsgInfo(), "a", long.class) :
                    HostInfo.getVerCode() < 8000 ? MField.FindField(RevokeMsgInfo(), "b", long.class):
                            MField.FindField(RevokeMsgInfo(),"f",long.class);;
            if (f != null) f.setAccessible(true);
            return f;
        }

        @Override
        public boolean startHook() {
            return false;
        }

        @Override
        public boolean isEnable() {
            return false;
        }

        private StringBuilder errCache = new StringBuilder();

        @Override
        public String getErrorInfo() {
            return super.getErrorInfo();
        }

        @Override
        public boolean check() {
            errCache.setLength(0);
            if (GroupUin() == null) errCache.append("GroupUin is null\n");
            if (OpUin() == null) errCache.append("OpUin is null\n");
            if (Sender() == null) errCache.append("Sender is null\n");
            if (IsTroop() == null) errCache.append("IsTroop is null\n");
            if (shmsgseq() == null) errCache.append("shmsgseq is null\n");

            return errCache.length() == 0;
        }
    }

}
