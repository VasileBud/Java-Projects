import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Iterator;

public class Magazin extends JFrame implements Totals {
    private JPanel mainPanel;
    private JTextField codField;
    private JTextField denumireField;
    private JTextField pretField;
    private JCheckBox faraGlutenCheckBox;
    private JButton adaugaProdusInCosButton;
    private JButton stergeProdusDinCosButton;
    private JList listaCumparaturi;
    private JButton puneProdusulPeBandaButton;
    private JList bandaList;
    private JButton totalCosButton;
    private JButton totalBandaButton;
    private JCheckBox a20DiscountCheckBox;

    ArrayList<Produs> produse = new ArrayList<Produs>();
    ArrayList<Produs> banda = new ArrayList<Produs>();
    DefaultListModel<Produs> listModel1 = new DefaultListModel();
    DefaultListModel<Produs> listModel2 = new DefaultListModel();

    public Magazin() {
        this.setTitle("Magazin");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(800, 500);
        this.setContentPane(mainPanel);
        this.setVisible(true);

        listaCumparaturi.setModel(listModel1);

        adaugaProdusInCosButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int cod = Integer.parseInt(codField.getText());
                String denumire = denumireField.getText();
                double pret = Double.parseDouble(pretField.getText());
                boolean faraGluten = faraGlutenCheckBox.isSelected();

                Produs p = new Produs(cod, denumire, pret, faraGluten);
                produse.add(p);
                listModel1.addElement(p);
            }
        });
        stergeProdusDinCosButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    int cod = Integer.parseInt(JOptionPane.showInputDialog("Introduceti codul produsului care va fi sters"));
                    Produs p = cautaProdus(produse, cod);
                    produse.remove(p);
                    listModel1.removeElement(p);
                } catch (ProdusNotFoundException exception) {
                    JOptionPane.showMessageDialog(mainPanel, exception.getMessage());
                }
            }
        });

        bandaList.setModel(listModel2);

        puneProdusulPeBandaButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int index = listaCumparaturi.getSelectedIndex();
                banda.add(produse.get(index));
                listModel2.addElement(produse.get(index));
                listModel1.remove(index);
                produse.remove(produse.get(index));
            }
        });
        totalCosButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(mainPanel, "Cosul de cumparaturi costa " + totalCos());
            }
        });
        totalBandaButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(mainPanel, "Banda de cumparaturi costa " + totalBanda());
            }
        });
    }

    public Produs cautaProdus(ArrayList<Produs> listaDeProduse, int cod) throws ProdusNotFoundException {
        Iterator<Produs> iterator = produse.iterator();
        while (iterator.hasNext()) {
            Produs p = iterator.next();
            if (p.getCodProdus() == cod) {
                return p;
            }
        }
        throw new ProdusNotFoundException("Produsul cu codul " + cod + " nu se  afla in cos.");
    }

    @Override
    public double totalCos() {
        double total = 0;
        Iterator<Produs> iterator = produse.iterator();
        while (iterator.hasNext()) {
            Produs p = iterator.next();
            total = total + p.getPret();
        }
        return total;
    }

    public double totalBanda() {
        double total = 0;
        Iterator<Produs> iterator = banda.iterator();
        while (iterator.hasNext()) {
            Produs p = iterator.next();
            total = total + p.getPret();
        }
        if (a20DiscountCheckBox.isSelected()) {
            total = total - (((double) 20 / 100) * total);
        }
        return total;
    }

    public static void main(String[] args) {
        Magazin magazin=new Magazin();
    }

}
