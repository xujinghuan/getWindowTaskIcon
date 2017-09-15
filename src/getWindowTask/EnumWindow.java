package getWindowTask;

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef.HWND;
import com.sun.jna.win32.StdCallLibrary;

/**
 * 枚举窗口
 * @author 徐经欢
 *
 */
public class EnumWindow {
	public interface User32 extends StdCallLibrary {
		User32 INSTANCE = (User32) Native.loadLibrary("user32", User32.class);

		interface WNDENUMPROC extends StdCallCallback {
			boolean callback(Pointer hWnd, Pointer arg);
		}

		boolean EnumWindows(WNDENUMPROC lpEnumFunc, Pointer arg);
	}

	public static void main(String[] args) {
		User32 user32 = User32.INSTANCE;

		user32.EnumWindows(new User32.WNDENUMPROC() {
			int count;
			public boolean callback(Pointer hWnd, Pointer userData) {
				
				System.out.println("Found window " + hWnd + ", total " + ++count);
				return true;
			}
		}, null);
	}
}
