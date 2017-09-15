package getWindowTask;

import com.sun.jna.platform.win32.BaseTSD;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinUser;


public class jnaTest {
	public static void main(String[] args) {

		WinDef.HWND hwnd = User32.INSTANCE.FindWindow
				(null, "QQ"); // 第一个参数是Windows窗体的窗体类，第二个参数是窗体的标题。不熟悉windows编程的需要先找一些Windows窗体数据结构的知识来看看，还有windows消息循环处理，其他的东西不用看太多。
		if (hwnd == null) {
			System.out.println("Excel is not running");
		}
		else{
			User32.INSTANCE.ShowWindow(hwnd, 9 );        // SW_RESTORE
			User32.INSTANCE.SetForegroundWindow(hwnd);   // bring to front

			String username = "yourQQnumber";
			for(Character c: username.toCharArray())
				sendChar(c);
		}
	}

	static WinUser.INPUT input = new WinUser.INPUT(  );
	static void  sendChar(char ch){

		input.type = new WinDef.DWORD( WinUser.INPUT.INPUT_KEYBOARD );
		input.input.setType("ki"); // Because setting INPUT_INPUT_KEYBOARD is not enough: https://groups.google.com/d/msg/jna-users/NDBGwC1VZbU/cjYCQ1CjBwAJ
		input.input.ki.wScan = new WinDef.WORD( 0 );
		input.input.ki.time = new WinDef.DWORD( 0 );
		input.input.ki.dwExtraInfo = new BaseTSD.ULONG_PTR( 0 );
		// Press
		input.input.ki.wVk = new WinDef.WORD( Character.toUpperCase(ch) ); // 0x41
		input.input.ki.dwFlags = new WinDef.DWORD( 0 );  // keydown

		User32.INSTANCE.SendInput( new WinDef.DWORD( 1 ), ( WinUser.INPUT[] ) input.toArray( 1 ), input.size() );

		// Release
		input.input.ki.wVk = new WinDef.WORD( Character.toUpperCase(ch) ); // 0x41
		input.input.ki.dwFlags = new WinDef.DWORD( 2 );  // keyup

		User32.INSTANCE.SendInput( new WinDef.DWORD( 1 ), ( WinUser.INPUT[] ) input.toArray( 1 ), input.size() );

	}
}
