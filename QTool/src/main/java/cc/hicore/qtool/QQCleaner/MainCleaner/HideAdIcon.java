package cc.hicore.qtool.QQCleaner.MainCleaner;

import android.content.Context;

import java.lang.reflect.Method;

import cc.hicore.HookItem;
import cc.hicore.ReflectUtils.MClass;
import cc.hicore.ReflectUtils.MMethod;
import cc.hicore.ReflectUtils.XPBridge;
import cc.hicore.UIItem;
import cc.hicore.qtool.XposedInit.ItemLoader.BaseHookItem;
import cc.hicore.qtool.XposedInit.ItemLoader.BaseUiItem;
import cc.hicore.qtool.XposedInit.ItemLoader.HookLoader;
import cc.hicore.qtool.XposedInit.MethodFinder;
import de.robv.android.xposed.XposedBridge;

@HookItem(isDelayInit = false,isRunInAllProc = false)
@UIItem(name = "隐藏主界面右上角入口",desc = "(不支持旧版QQ)可能包含小世界入口等",groupName = "主界面净化",targetID = 2,type = 1,id = "HideMainAdEntry")
public class HideAdIcon extends BaseHookItem implements BaseUiItem {
    boolean IsEnable;
    @Override
    public boolean startHook() throws Throwable {
        Method[] m = getMethod();
        XPBridge.HookBefore(m[0],param -> {
            if (IsEnable){
                param.setResult(null);
            }
        });
        return true;
    }

    @Override
    public boolean isEnable() {
        return IsEnable;
    }

    @Override
    public boolean check() {
        return getMethod()[0]!=null;
    }

    @Override
    public void SwitchChange(boolean IsCheck) {
        IsEnable = IsCheck;
        if (IsCheck) HookLoader.CallHookStart(HideAdIcon.class.getName());
    }

    @Override
    public void ListItemClick(Context context) {

    }
    public Method[] getMethod(){
        Method[] m = new Method[1];
        m[0] = MMethod.FindMethod(MClass.loadClass("com.tencent.mobileqq.activity.ConversationTitleBtnCtrl"),"a",void.class,new Class[0]);
        if (m[0] == null){
            m[0] = MethodFinder.findMethodFromCache("HideAdIcon");
            if (m[0] == null){
                MethodFinder.NeedReportToFindMethod("Conver_Init","隐藏主界面右上角","#666666",ma->ma.getDeclaringClass().getName().equals("com.tencent.mobileqq.activity.home.Conversation"));
                MethodFinder.NeedReportToFindMethodConnectTag("HideAdIcon","隐藏主界面右上角","Conver_Init",ma -> ma.getParameterCount() == 0 && ma.getDeclaringClass().getName().equals("com.tencent.mobileqq.activity.ConversationTitleBtnCtrl"));
            }
        }
        return m;
    }
}
