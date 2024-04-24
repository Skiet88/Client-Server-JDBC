package za.ac.cput.StudentEnrolment;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author PURE
 */
public class StudentGUI extends JFrame implements ActionListener {

    private JPanel panelNorth, panelCenter, contentContainerPanel, navigationPanel, nameNStdNumPanel;
    private JComboBox subjectIdCBO;
    private JButton enrollBtn, cancelEnrollBtn, viewAllEnrollmentsBtn, logoutBtn;
    private JLabel headingLbl, studentNameLbl, studentNumberLbl, yourSubjectsLbl, availableSubjectsLbl, welcomeLbl;
    private Font f1, f2;
    //private JMenu menu;
    DefaultTableModel tableModel, tableModel2;
    private JTable subjectsTable, registeredSubjects;
    private Student student;
    private ObjectInputStream in;
    private ObjectOutputStream out;
    private static Socket socket;
    private Thread serverThread;
    private Thread clientThread;
    private StudentEnrolmentSystemServer runServer;
    ArrayList<Subject> enrolledSubjectList;
    ArrayList<Subject> allSubjectsList;

    public StudentGUI(Student student) {
        super("Student Gui");
        this.student = student;
        runServer = new StudentEnrolmentSystemServer();

        //Font
        f1 = new Font("Arial", Font.PLAIN, 12);
        f2 = new Font("Arial", Font.BOLD, 14);

        //panels
        panelNorth = new JPanel();
        panelNorth.setBackground(Color.black);
        panelNorth.setForeground(Color.white);

        panelCenter = new JPanel();
        
        contentContainerPanel = new JPanel();
        navigationPanel = new JPanel();
        nameNStdNumPanel = new JPanel();
        
        //labels
        headingLbl = new JLabel("Student GUI");
        headingLbl.setForeground(Color.white);

        welcomeLbl = new JLabel("Welcome,");
        welcomeLbl.setFont(f2);

        studentNameLbl = new JLabel(student.getStdName() + " " + student.getStdSurname());
        studentNameLbl.setFont(f1);

        studentNumberLbl = new JLabel(student.getStddNum());
        studentNumberLbl.setFont(f1);

        yourSubjectsLbl = new JLabel("Your Subjects");
        yourSubjectsLbl.setFont(f2);
        availableSubjectsLbl = new JLabel("Available Subjects");

        //Buttons
        ImageIcon enrollIcon = new ImageIcon("C:\\Users\\PURE\\Documents\\NetBeansProjects\\StudentEnrolmentSystem\\enroll.jpg");
        enrollBtn = new JButton(enrollIcon);
        enrollBtn.setText("Enroll");

        ImageIcon cancelIcon = new ImageIcon("C:\\Users\\PURE\\Documents\\NetBeansProjects\\StudentEnrolmentSystem\\cancel.jpg");
        cancelEnrollBtn = new JButton(cancelIcon);
        cancelEnrollBtn.setText("Cancel Enrollment");

        viewAllEnrollmentsBtn = new JButton("All Offered Subjects");

        ImageIcon exitIcon = new ImageIcon("C:\\Users\\PURE\\Documents\\NetBeansProjects\\StudentEnrolmentSystem\\exit.jpg");

        logoutBtn = new JButton(exitIcon);
        logoutBtn.setText("Logout");

        //JTables
        //table1
        tableModel = new DefaultTableModel();
        subjectsTable = new JTable(tableModel);
        tableModel.addColumn("Subject ID");
        tableModel.addColumn("Description");
        tableModel.addColumn("year");
        subjectsTable.setPreferredScrollableViewportSize(new Dimension(700, 300));

//        //table2
//        tableModel2 = new DefaultTableModel();
//        registeredSubjects = new JTable(tableModel2);
//        tableModel2.addColumn("Subject ID");
//        tableModel2.addColumn("Description");
//        tableModel2.addColumn("Year");
//CBO
        setGui();
        startThreads();
    }

    public void populateEnrollmentComboBox() {
        try {
            out.writeObject("retrieveSubjects");
            out.flush();

            allSubjectsList = (ArrayList<Subject>) in.readObject();

            for (Subject subject : allSubjectsList) {
                subjectIdCBO.addItem(subject.getSubjectCode());
            }

        } catch (IOException ex) {
            Logger.getLogger(StudentGUI.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(StudentGUI.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public void populateCancelEnrollmentComboBox() {
        for (Subject subject : enrolledSubjectList) {
            subjectIdCBO.addItem(subject.getSubjectCode());
        }

    }

    public void resetTable() {
        tableModel.setRowCount(0);
        populateTable();
    }

    public void populateTable() {
        try {

            out.writeObject("searchStudent" + student.getStddNum());
            out.flush();

            enrolledSubjectList = (ArrayList<Subject>) in.readObject();

            if (enrolledSubjectList.isEmpty()) {

                tableModel.addRow(new Object[]{"Not yet enrolled in any Subject "});

            } else {
                for (int i = 0; i < enrolledSubjectList.size(); i++) {
                    String subjectCode = enrolledSubjectList.get(i).getSubjectCode();
                    String subjectName = enrolledSubjectList.get(i).getSubjectName();
                    String duration = enrolledSubjectList.get(i).getDuration();
                    Object[] rowData = {subjectCode, subjectName, duration};
                    tableModel.addRow(rowData);
                }
            }

        } catch (IOException ex) {
            JOptionPane.showMessageDialog(null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(StudentGUI.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void setEnrollPanel() {
        JPanel enrollPanel = new JPanel();
        subjectIdCBO = new JComboBox();
        populateEnrollmentComboBox();
        JLabel studentNumberLbl = new JLabel("Student Number :");
        JLabel firstNameLbl = new JLabel("First Name :");
        JLabel surnameLbl = new JLabel("Surname :");
        JLabel subjectLbl = new JLabel("Subject:");
        JLabel durationLbl = new JLabel("Duration:");

        JTextField studentNumberTxt = new JTextField();
        studentNumberTxt.setEditable(false);
        studentNumberTxt.setText(student.getStddNum());

        JTextField firstNameTxt = new JTextField();
        firstNameTxt.setEditable(false);
        firstNameTxt.setText(student.getStdName());

        JTextField surnameTxt = new JTextField();
        surnameTxt.setEditable(false);
        surnameTxt.setText(student.getStdSurname());

        JTextField durationTxt = new JTextField();
        durationTxt.setEditable(false);

        enrollPanel.setLayout(new GridLayout(5, 2));

        enrollPanel.add(studentNumberLbl);
        enrollPanel.add(studentNumberTxt);

        enrollPanel.add(firstNameLbl);
        enrollPanel.add(firstNameTxt);

        enrollPanel.add(surnameLbl);
        enrollPanel.add(surnameTxt);

        enrollPanel.add(subjectLbl);
        enrollPanel.add(subjectIdCBO);

        enrollPanel.add(durationLbl);
        enrollPanel.add(durationTxt);

        subjectIdCBO.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (e.getSource() == subjectIdCBO) {
                    for (int i = 0; i < allSubjectsList.size(); i++) {
                        if (subjectIdCBO.getSelectedItem().toString().equals(allSubjectsList.get(i).getSubjectCode())) {
                            durationTxt.setText(allSubjectsList.get(i).getDuration());
                        };
                    }

                }
            }

        });

        int result = JOptionPane.showConfirmDialog(null, enrollPanel, "Enroll for a Subject", JOptionPane.OK_CANCEL_OPTION);

        if (result == JOptionPane.OK_OPTION) {
            try {

                Enrollment enrollment = new Enrollment(studentNumberTxt.getText(), subjectIdCBO.getSelectedItem().toString(), "2024");

                out.writeObject(enrollment);
                out.flush();

                String response = (String) in.readObject();

                JOptionPane.showMessageDialog(null, response);

                resetTable();
            } catch (IOException ex) {
                Logger.getLogger(StudentGUI.class.getName()).log(Level.SEVERE, null, ex);
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(StudentGUI.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public void fillSubjectTable() {
        ArrayList<Subject> subjects;
        try {
            out.writeObject("retrieveSubjects");
            out.flush();

            subjects = (ArrayList) in.readObject();

            JPanel panel = new JPanel();
            panel.setLayout(new FlowLayout());
            DefaultTableModel tableModel2 = new DefaultTableModel();
            JTable subjectsTable2 = new JTable(tableModel2);

            subjectsTable2.setModel(tableModel2);
            tableModel2 = (DefaultTableModel) subjectsTable2.getModel();
            tableModel2.setRowCount(0);

            tableModel2.addColumn("Subject ID");
            tableModel2.addColumn("Description");
            tableModel2.addColumn("Year");

            for (int i = 0; i < subjects.size(); i++) {

                String subjectCode = subjects.get(i).getSubjectCode();
                String subjectName = subjects.get(i).getSubjectName();
                String duration = subjects.get(i).getDuration();

                Object[] row = {subjectCode, subjectName, duration};
                tableModel2.addRow(row);

            }

            panel.add(new JScrollPane(subjectsTable2), BorderLayout.CENTER);

            int result = JOptionPane.showConfirmDialog(null, panel, "Available Subjects", JOptionPane.PLAIN_MESSAGE);

        } catch (IOException ex) {
            Logger.getLogger(StudentGUI.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(StudentGUI.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public void setenrollCancelationPanel() {
        JPanel cancelEnrollPanel = new JPanel();
        subjectIdCBO = new JComboBox();
        populateCancelEnrollmentComboBox();
        JLabel studentNumberLbl = new JLabel("Student Number :");
        JLabel firstNameLbl = new JLabel("First Name :");
        JLabel surnameLbl = new JLabel("Surname :");
        JLabel subjectLbl = new JLabel("Subject:");
        JLabel durationLbl = new JLabel("Duration:");

        JTextField studentNumberTxt = new JTextField();
        studentNumberTxt.setEditable(false);
        studentNumberTxt.setText(student.getStddNum());

        JTextField firstNameTxt = new JTextField();
        firstNameTxt.setEditable(false);
        firstNameTxt.setText(student.getStdName());

        JTextField surnameTxt = new JTextField();
        surnameTxt.setEditable(false);
        surnameTxt.setText(student.getStdSurname());

        JTextField durationTxt = new JTextField();
        durationTxt.setEditable(false);

        cancelEnrollPanel.setLayout(new GridLayout(5, 2));

        cancelEnrollPanel.add(studentNumberLbl);
        cancelEnrollPanel.add(studentNumberTxt);

        cancelEnrollPanel.add(firstNameLbl);
        cancelEnrollPanel.add(firstNameTxt);

        cancelEnrollPanel.add(surnameLbl);
        cancelEnrollPanel.add(surnameTxt);

        cancelEnrollPanel.add(subjectLbl);
        cancelEnrollPanel.add(subjectIdCBO);

        cancelEnrollPanel.add(durationLbl);
        cancelEnrollPanel.add(durationTxt);

        subjectIdCBO.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (e.getSource() == subjectIdCBO) {
                    for (int i = 0; i < enrolledSubjectList.size(); i++) {
                        if (subjectIdCBO.getSelectedItem().toString().equals(enrolledSubjectList.get(i).getSubjectCode())) {
                            durationTxt.setText(enrolledSubjectList.get(i).getDuration());
                        };
                    }

                }
            }

        });

        int result = JOptionPane.showConfirmDialog(null, cancelEnrollPanel, "Enrollment cancellation", JOptionPane.OK_CANCEL_OPTION);

        if (result == JOptionPane.OK_OPTION) {
            try {

                int choice = JOptionPane.showConfirmDialog(null, "Are you sure you want to unenroll for: " + subjectIdCBO.getSelectedItem().toString(), "Cancel Enrollment", JOptionPane.YES_NO_OPTION);
                if (choice == 0) {
                    out.writeObject("CancelEnroll" + student.getStddNum() + "#" + subjectIdCBO.getSelectedItem().toString());
                    out.flush();
                    String response = (String) in.readObject();
                    JOptionPane.showMessageDialog(null, response);
                    resetTable();

                }

            } catch (IOException ex) {
                Logger.getLogger(StudentGUI.class.getName()).log(Level.SEVERE, null, ex);
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(StudentGUI.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public void setGui() {
        //setting layout for panels
        panelNorth.setLayout(new FlowLayout());
        contentContainerPanel.setLayout(new GridLayout(4, 1));
        contentContainerPanel.setPreferredSize(new Dimension(800, 400));
        navigationPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
        nameNStdNumPanel.setLayout(new GridLayout(3, 1));

        //addding components to panels
        //Adding panelNorth Components
        panelNorth.add(headingLbl);

        //Name and STD num Panel
        nameNStdNumPanel.add(welcomeLbl);
        nameNStdNumPanel.add(studentNameLbl);
        nameNStdNumPanel.add(studentNumberLbl);

        //Adding navigationPanel Components
        navigationPanel.add(enrollBtn);
        navigationPanel.add(cancelEnrollBtn);
        navigationPanel.add(viewAllEnrollmentsBtn);
        navigationPanel.add(logoutBtn);

        //Adding centerPanel Components
        contentContainerPanel.add(navigationPanel);
        contentContainerPanel.add(nameNStdNumPanel);
        contentContainerPanel.add(yourSubjectsLbl);
        contentContainerPanel.add(new JScrollPane(subjectsTable));

        panelCenter.add(contentContainerPanel);
        panelCenter.setBackground(Color.white);

        //ActionListeners
        enrollBtn.addActionListener(this);
        viewAllEnrollmentsBtn.addActionListener(this);
        cancelEnrollBtn.addActionListener(this);
        logoutBtn.addActionListener(this);

        this.add(panelNorth, BorderLayout.NORTH);
        this.add(panelCenter, BorderLayout.CENTER);

        this.pack();
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setSize(900, 500);
        this.setVisible(true);

    }

    public StudentEnrolmentSystemServer startThreads() {

        serverThread = new Thread(() -> {
            System.out.println("about to star server");
            runServer.Connect();
            System.out.println("started server");

        });

        System.out.println("about to call star method 1");
        serverThread.start();
        System.out.println("called start method");
        clientThread = new Thread(() -> {

            try {
                Thread.sleep(1000);
            } catch (InterruptedException ex) {
                Logger.getLogger(LoginGUI.class.getName()).log(Level.SEVERE, null, ex);
            }
            try {
                System.out.println("about to connect to servr");
                socket = new Socket("127.0.0.1", 12345);
                System.out.println("pssed connecting line");

            } catch (IOException ex) {
                JOptionPane.showMessageDialog(null, "Connection failed in client");
            }

            try {
                out = new ObjectOutputStream(socket.getOutputStream());
                out.flush();
                in = new ObjectInputStream(socket.getInputStream());
                resetTable();
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(null, "Failed to get streams in client");
            }
        });

        clientThread.start();

        return runServer;
    }

    public void closeConnection() {

        try {
            runServer.closeConnection();
            in.close();
            out.close();
            socket.close();
            System.out.println("Client Closed connection");

            serverThread.interrupt();
            //serverThread.join();
            clientThread.interrupt();
            // clientThread.join();
            //runServer.closeConnection();

        } catch (IOException ex) {
            System.out.println("ERROR: " + ex);
        }
//        } catch (InterruptedException ex) {
//            Logger.getLogger(LoginGUI.class.getName()).log(Level.SEVERE, null, ex);
//        }
    }

    public static void main(String[] args) {

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == enrollBtn) {

            setEnrollPanel();

        } else if (e.getSource() == cancelEnrollBtn) {
            setenrollCancelationPanel();
        } else if (e.getSource() == viewAllEnrollmentsBtn) {

            fillSubjectTable();

        }

        if (e.getSource() == logoutBtn) {
            closeConnection();
            this.dispose();
            new LoginGUI();

        }
    }
}
