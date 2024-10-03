import org.mockito.Mockito;

import businessLogic.BLFacade;
import gui.MainGUI;

public class PruebaGUI {
	static BLFacade appFacadeInterface = Mockito.mock(BLFacade.class);// @mock BLFacade mock <-- es lo mismo

	public static void main(String args[]) {
		Mockito.doReturn(true).when(appFacadeInterface).isRegistered("a","a");
		Mockito.doReturn("Driver").when(appFacadeInterface).getMotaByUsername(Mockito.anyString());  //da igual el parametro que le apse, siempre devuelve driver
		MainGUI a = new MainGUI();
		MainGUI.setBussinessLogic(appFacadeInterface);
		a.setVisible(true);
	}
}
