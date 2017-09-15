package getWindowTask;

import java.awt.Image;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.filechooser.FileSystemView;

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.Shell32;
import com.sun.jna.platform.win32.Tlhelp32;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinDef.HICON;
import com.sun.jna.platform.win32.WinNT;
import com.sun.jna.win32.W32APIOptions;

/**
 * 获取 window任务栏应用程序的图标（也就是任务管理器中前台进程）
 * @author 徐经欢 2017-09-14 add
 */
public class Maintest {

	public interface ProcessPathKernel32 extends Kernel32 {
		class MODULEENTRY32 extends Structure {
			public static class ByReference extends MODULEENTRY32 implements Structure.ByReference {
				public ByReference() {}
				public ByReference(Pointer memory) {
					super(memory);
				}
			}
			public MODULEENTRY32() {
				dwSize = new WinDef.DWORD(size());
			}

			public MODULEENTRY32(Pointer memory) {
				super(memory);
				read();
			}

			public DWORD dwSize;
			public DWORD th32ModuleID;
			public DWORD th32ProcessID;
			public DWORD GlblcntUsage;
			public DWORD ProccntUsage;
			public Pointer modBaseAddr;
			public DWORD modBaseSize;
			public HMODULE hModule;
			public char[] szModule = new char[255+1]; // MAX_MODULE_NAME32
			public char[] szExePath = new char[MAX_PATH];
			public String szModule() { return Native.toString(this.szModule); }
			public String szExePath() { return Native.toString(this.szExePath); }
			@Override
			protected List<String> getFieldOrder() {
				return Arrays.asList(new String[] {
						"dwSize", "th32ModuleID", "th32ProcessID", "GlblcntUsage", "ProccntUsage", "modBaseAddr", "modBaseSize", "hModule", "szModule", "szExePath"
				});
			}
		}

		ProcessPathKernel32 INSTANCE = (ProcessPathKernel32)Native.loadLibrary(ProcessPathKernel32.class, W32APIOptions.UNICODE_OPTIONS);
		boolean Module32First(HANDLE hSnapshot, MODULEENTRY32.ByReference lpme);
		boolean Module32Next(HANDLE hSnapshot, MODULEENTRY32.ByReference lpme);
	}

	public static void main(String[] args) throws IOException {

		HICON[] a=new WinDef.HICON[12];
		HICON[] b=new WinDef.HICON[11];
		Set<Integer> Pids=EnumWindow.getTaskPID();//获取窗口进程的PID
		int c=1;
		Kernel32 kernel32 = (Kernel32) Native.loadLibrary(Kernel32.class, W32APIOptions.DEFAULT_OPTIONS);
		Tlhelp32.PROCESSENTRY32.ByReference processEntry = new Tlhelp32.PROCESSENTRY32.ByReference();
		WinNT.HANDLE processSnapshot = 
				kernel32.CreateToolhelp32Snapshot(Tlhelp32.TH32CS_SNAPPROCESS, new WinDef.DWORD(0));
		try {
			while (kernel32.Process32Next(processSnapshot, processEntry)) {
				//processEntry.th32ProcessID  程序的PID
				//Native.toString(processEntry.szExeFile) 程序的名字（xx.exe）
				WinNT.HANDLE moduleSnapshot =kernel32.CreateToolhelp32Snapshot(Tlhelp32.TH32CS_SNAPMODULE, processEntry.th32ProcessID);
				if(Pids.contains(processEntry.th32ProcessID.intValue())){
					String exeName=Native.toString(processEntry.szExeFile).substring(0,Native.toString(processEntry.szExeFile).indexOf(".exe"));
					if(exeName.toLowerCase().equals("shellexperiencehost")||exeName.toLowerCase().equals("syntpenh")){//ShellExperienceHost为开始菜单外壳,syntpenh为触摸板相关程序
						continue;
					}
					try {
						ProcessPathKernel32.MODULEENTRY32.ByReference me = new ProcessPathKernel32.MODULEENTRY32.ByReference();
						ProcessPathKernel32.INSTANCE.Module32First(moduleSnapshot, me);
						//me.szExePath() //程序（xx.exe）所在路径
						Shell32.INSTANCE.ExtractIconEx(me.szExePath(), 0, a, b, c);
						if(a.length>0&&Native.toString(processEntry.szExeFile)!=null&&Native.toString(processEntry.szExeFile).length()>0&&Native.toString(processEntry.szExeFile).indexOf(".exe")>=0){//判断是否有图标
							String fileName=Native.toString(processEntry.szExeFile).substring(0,Native.toString(processEntry.szExeFile).indexOf(".exe"))+".jpg";
							if (me.szExePath()!=null&&me.szExePath()!="") {
								File file=new File(me.szExePath());//.exe文件
								File imgFile=new File("C:\\windowTaskBarIcon\\"+fileName);
								if (!imgFile.exists()) {
									imgFile.mkdirs();
								}
								Image image=((ImageIcon) FileSystemView.getFileSystemView().getSystemIcon(file)).getImage();
								ImageIO.write((RenderedImage) image,"jpg", imgFile);
							}
						}
					}
					finally {
						kernel32.CloseHandle(moduleSnapshot);
					}
				}
			}
		}
		finally {
			kernel32.CloseHandle(processSnapshot);
		}
	}

}
