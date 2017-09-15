package getWindowTask;

import java.util.HashSet;
import java.util.Set;

import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef.HWND;
import com.sun.jna.ptr.IntByReference;

/**
 * 枚举窗口（获得已经window打开窗口的PID）
 * @author 徐经欢
 *
 */
public class EnumWindow {
	public static Set<Integer> getTaskPID() {
		User32 user32 = User32.INSTANCE;
		Set<Integer> set=new HashSet<Integer>();
		IntByReference i=new IntByReference();//放PID
		user32.EnumWindows(new User32.WNDENUMPROC() {
			public boolean callback(HWND h, Pointer p) {
				user32.GetWindowThreadProcessId(h, i);//获取窗口的PID
				if(user32.IsWindow(h)&&user32.IsWindowEnabled(h)&&user32.IsWindowVisible(h)){
					set.add(i.getValue());
				}
				return true;
			}
		}, null);
		return set;
	}
}
