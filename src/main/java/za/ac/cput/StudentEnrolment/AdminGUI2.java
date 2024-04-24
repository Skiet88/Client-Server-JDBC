package za.ac.cput.StudentEnrolment;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.ScrollPane;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import static javax.swing.WindowConstants.EXIT_ON_CLOSE;
import za.ac.cput.StudentEnrolment.Student;

/**
 *
 * @author PURE
 */
public class AdminGUI2 extends JFrame implements ActionListener {

    private static ObjectInputStream in;
    private static ObjectOutputStream out;
    private static JPanel northPanel, centerPanel, southPanel, searchPanel, labelPanel;
    private static JLabel subjCodeLbl, subjNameLbl, searchLbl;
    private static JTextField subjCodeTxt, subjNameTxt, searchField;
    private static JTextArea recordsTextArea;
    private static JComboBox combobox;
    private static JButton retrieveEnrollment, cancelBtn, addSubjectButton, exitButton, outersearchButton, deleteButton;;
    private static Socket socket;
    private Thread serverThread, clientThread;
    private StudentEnrolmentSystemServer runServer;

    public AdminGUI2() {
        runServer = new StudentEnrolmentSystemServer();
        createAndShowGUI();

        startThreads();
    }

    private void createAndShowGUI() {

        northPanel = new JPanel();
        northPanel.setLayout(new GridLayout(2, 1));
        JLabel title = new JLabel("Student Enrollment System");
        title.setFont(new Font("Arial", Font.BOLD, 20));
        title.setForeground(Color.WHITE);
        labelPanel = new JPanel();
        labelPanel.add(title);
        labelPanel.setBackground(Color.DARK_GRAY);
        northPanel.add(labelPanel);

        retrieveEnrollment = new JButton("Retrieve enrollments");
        addSubjectButton = new JButton("Add subject");
        exitButton = new JButton("Log Out");

        deleteButton = new JButton("Delete");
        outersearchButton = new JButton("Search");

        searchField = new JTextField();
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout());
        buttonPanel.add(addSubjectButton);
        buttonPanel.add(retrieveEnrollment);
        buttonPanel.add(deleteButton);
        buttonPanel.add(outersearchButton);
        buttonPanel.add(exitButton);
        northPanel.add(buttonPanel);
        this.add(northPanel, BorderLayout.NORTH);

        southPanel = new JPanel();
        southPanel.setLayout(new GridLayout(1, 1));

        recordsTextArea = new JTextArea();
        southPanel.add(new JScrollPane(recordsTextArea));
        this.add(southPanel, BorderLayout.CENTER);

        //  this.add(centerPanel, BorderLayout.CENTER);
        retrieveEnrollment.addActionListener(this);
        addSubjectButton.addActionListener(this);
        exitButton.addActionListener(this);

        deleteButton.addActionListener(this);

        outersearchButton.addActionListener(this);

        this.pack();
        this.setSize(550, 250);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setVisible(true);
    }

    public void startThreads() {
        //Open Server Thread
        serverThread = new Thread(() -> {

            //Calling Connect() method to start the server
            runServer.Connect();

        });
        //Server Thread begins execution
        serverThread.start();

        //Open Client Thread
        clientThread = new Thread(() -> {

            try {
                Thread.sleep(1000);
            } catch (InterruptedException ex) {
                Logger.getLogger(LoginGUI.class.getName()).log(Level.SEVERE, null, ex);
            }
            try {
                //Connect to server
                socket = new Socket("127.0.0.1", 12345);

            } catch (IOException ex) {
                JOptionPane.showMessageDialog(null, "Connection failed in client");
            }
            //Get streams
            try {
                out = new ObjectOutputStream(socket.getOutputStream());
                out.flush();
                in = new ObjectInputStream(socket.getInputStream());
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(null, "Failed to get streams in client");
            }
        });
        //Client Thread begins execution
        clientThread.start();
    }

    //Panel for the search function
    public void makePanelVisible() {
        String selectedValue;

        combobox = new JComboBox(new String[]{"Select", "Search Student's Subjects by Student Number", "Search Enrolled Students By Subject Code"});

        JButton cancelBtn = new JButton("Cancel");
        JButton searchButton = new JButton("Search");
        JPanel upperPanel = new JPanel();

        JPanel centerPanel = new JPanel();

        centerPanel.setLayout(new GridLayout(3, 1));
        centerPanel.add(combobox);
        centerPanel.add(searchField);

        JPanel panelButton = new JPanel();

        panelButton.setLayout(new GridLayout(1, 2));

        panelButton.add(searchButton);

        centerPanel.add(panelButton);

        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (e.getSource() == searchButton) {
                    searchEnrollment();
                }
            }
        });

        int result = JOptionPane.showConfirmDialog(null, centerPanel, "Delete Selection", JOptionPane.OK_CANCEL_OPTION);

        if (result == JOptionPane.OK_OPTION) {
            selectedValue = combobox.getSelectedItem().toString();

        }
    }

    //Method to add a Subject in the database
    private static void addSubject() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(3, 2));

        JTextField subject_CodeTxt = new JTextField(10);
        JTextField subject_DescriptionTxt = new JTextField(10);
        JTextField subject_DurationTxt = new JTextField(10);
        panel.add(new JLabel("Subject Code:"));
        panel.add(subject_CodeTxt);
        panel.add(new JLabel("Subject Description:"));
        panel.add(subject_DescriptionTxt);
        panel.add(new JLabel("Subject Duration:"));
        panel.add(subject_DurationTxt);

        // Show the JOptionPane with the custom panel
        int result = JOptionPane.showConfirmDialog(null, panel, "Enter Subject Details", JOptionPane.OK_CANCEL_OPTION);

        if (result == JOptionPane.OK_OPTION) {
            String subject_Code = subject_CodeTxt.getText();
            String subject_Description = subject_DescriptionTxt.getText();
            String subject_Duration = subject_DurationTxt.getText();

            if (!(subject_Code == null || subject_Description == null || subject_Duration == null)) {
                try {

                    Subject newSubject = new Subject(subject_Code, subject_Description, subject_Duration);

                    out.writeObject(newSubject);
                    out.flush();
           
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(null, "Failed to add subject");
                }
            } else {
                JOptionPane.showMessageDialog(null, "Missing Values");

            }
        }
    }

    //Method to retrieve all enrollments
    private static void retrieveEnrollments() {
        try {
            out.writeObject("retrieveEnrollments");
            out.flush();

            ArrayList<Enrollment> list = (ArrayList) in.readObject();

            recordsTextArea.setText("");
            recordsTextArea.append("Student Enrolments:\n");
            recordsTextArea.append("Student_Number \tSubject_Code \tYear\n");
            for (int i = 0; i < list.size(); i++) {
                recordsTextArea.append(list.get(i).toString() + "\n");
            }

        } catch (IOException ex) {
            JOptionPane.showMessageDialog(null, "Failed to retrieve enrollments");
        } catch (ClassNotFoundException ex) {
            JOptionPane.showMessageDialog(null, "Class not found: " + ex);
        }
    }

    //Method to delete Students and Subjects
    private void deleteEnrollment() {
        JComboBox combobox = new JComboBox(new String[]{});
        JComboBox SubjectSelection = new JComboBox(new String[]{"Delete Student", "Delete Subject"});

        JPanel panel = new JPanel();
        panel.add(new JLabel("What do you want to delete, Select: "));
        panel.add(SubjectSelection);

        int result = JOptionPane.showConfirmDialog(null, panel, "Delete Selection", JOptionPane.OK_CANCEL_OPTION);

        if (result == JOptionPane.OK_OPTION) {
            String selectedValue = SubjectSelection.getSelectedItem().toString();

            if (selectedValue.equals("Delete Student")) {
                try {
                    out.writeObject("getAlltudents");
                    out.flush();

                    ArrayList<Student> items = (ArrayList) in.readObject();
                    for (int i = 0; i < items.size(); i++) {
                        combobox.addItem(items.get(i).getStddNum());
                    }                  
                    

                } catch (IOException ex) {
                    Logger.getLogger(AdminGUI2.class.getName()).log(Level.SEVERE, null, ex);
                } catch (ClassNotFoundException ex) {
                    Logger.getLogger(AdminGUI2.class.getName()).log(Level.SEVERE, null, ex);
                }

            }
            if (selectedValue.equals("Delete Subject")) {
                try {
                    out.writeObject("retrieveSubjects");
                    out.flush();

                    ArrayList<Subject> items = (ArrayList) in.readObject();
                    for (int i = 0; i < items.size(); i++) {
                        combobox.addItem(items.get(i).getSubjectCode());
                    }
                } catch (IOException ex) {
                    Logger.getLogger(AdminGUI2.class.getName()).log(Level.SEVERE, null, ex);
                } catch (ClassNotFoundException ex) {
                    Logger.getLogger(AdminGUI2.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

            JPanel panel2 = new JPanel();
            panel2.add(new JLabel("Select Item to Delete"));
            panel2.add(combobox);

            int result2 = JOptionPane.showConfirmDialog(null, panel2, "Delete Selection", JOptionPane.OK_CANCEL_OPTION);

            if (result2 == JOptionPane.OK_OPTION) {
                String selectedItem = combobox.getSelectedItem().toString();

                try {
                    if (selectedValue.equals("Delete Student")) {
                        out.writeObject("DeleteStudent" + selectedItem);
                    }
                    if (selectedValue.equals("Delete Subject")) {
                        out.writeObject("DeleteSubject" + selectedItem);
                    }
                    out.flush();
                } catch (IOException ex) {
                    Logger.getLogger(AdminGUI2.class.getName()).log(Level.SEVERE, null, ex);
                }

            }
        }
    }

    //Method to Search for Students and Subjects
    private static void searchEnrollment() {
        try {
            if (!searchField.getText().equals("")) {
                if (combobox.getSelectedItem().toString().equalsIgnoreCase("Search Student's Subjects by Student Number")) {
                    out.writeObject("searchStudent" + searchField.getText());
                    out.flush();
                    searchField.setText(null);

                    ArrayList<Subject> response = (ArrayList) in.readObject();

                    if (!response.isEmpty()) {
                        recordsTextArea.setText("");
                        recordsTextArea.append("STUDENT'S SUBJECTS:\n");
                        recordsTextArea.append("Subject_Code \tDescription \t\tDuration\n");
                        for (int i = 0; i < response.size(); i++) {
                            recordsTextArea.append(response.get(i).toString() + "\n");
                        }
                    } else {
                        searchField.setText(null);
                        JOptionPane.showMessageDialog(null, "No result found!");
                    }

                } else if (combobox.getSelectedItem().toString().equalsIgnoreCase("Search Enrolled Students By Subject Code")) {
                    out.writeObject("searchSubject" + searchField.getText());
                    out.flush();
                    ArrayList<Student> response = (ArrayList) in.readObject();
                    searchField.setText(null);

                    if (!response.isEmpty()) {
                        recordsTextArea.setText("");
                        recordsTextArea.append("ENROLLED STUDENTS IN SUBJECT:\n");
                        recordsTextArea.append("Student_Number \tName \tSurname\n");
                        for (int i = 0; i < response.size(); i++) {
                            recordsTextArea.append(response.get(i) + "\n");
                        }
                    } else {
                        searchField.setText(null);
                        JOptionPane.showMessageDialog(null, "No result found!");
                    }
                }
            }

        } catch (IOException ex) {
            Logger.getLogger(StudentGUI.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(StudentGUI.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    //various operations performed based on the button clicked
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == retrieveEnrollment) {
            retrieveEnrollments();
        } else if (e.getSource() == addSubjectButton) {
            recordsTextArea.setText("");
            addSubject();
        } else if (e.getSource() == deleteButton) {
            recordsTextArea.setText("");
            deleteEnrollment();
        } else if (e.getSource() == outersearchButton) {
            recordsTextArea.setText("");
            makePanelVisible();
        } else if (e.getSource() == exitButton) {
            disconnect();
            new LoginGUI();
            this.dispose();

        }
    }

    //Method to close all connections, streams and sockets.
    public void disconnect() {
        try {
            runServer.closeConnection();
            in.close();
            out.close();
            socket.close();
            System.out.println("Client Closed connection");

            serverThread.interrupt();
            clientThread.interrupt();

        } catch (IOException ex) {
            System.out.println("ERROR: " + ex);
        }
    }

    public static void main(String[] args) {
        AdminGUI2 guiObj = new AdminGUI2();
    }

}
