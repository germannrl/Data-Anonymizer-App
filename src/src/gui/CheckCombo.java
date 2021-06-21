package src.gui;

import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import javax.swing.JComboBox;
import java.awt.event.ActionEvent;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.JCheckBox;
import java.awt.Component;
import java.awt.Color;


public class CheckCombo
extends JComboBox {
CheckComboRenderer renderer;
public CheckCombo() {
renderer = new CheckComboRenderer();
setRenderer(renderer);
addActionListener(this);
}


public void setPopupVisible(boolean flag) {
}

}

class CheckComboRenderer
implements ListCellRenderer {
JCheckBox checkBox;

public CheckComboRenderer() {
checkBox = new JCheckBox();

}

public Component getListCellRendererComponent(JList list,
Object value,
int index,
boolean isSelected,
boolean cellHasFocus) {
CheckComboStore store = (CheckComboStore) value;

checkBox.setText(store.id);
checkBox.setSelected( ( (Boolean) store.state).booleanValue());
checkBox.setBackground(isSelected ? Color.LIGHT_GRAY : Color.white);
checkBox.setForeground(isSelected ? Color.white : Color.black);
return checkBox;
}
}

class CheckComboStore {
String id;
Boolean state;

public CheckComboStore(String id, Boolean state) {
this.id = id;
this.state = state;
}
}