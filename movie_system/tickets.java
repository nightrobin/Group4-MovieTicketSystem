package movie_system;

import java.awt.Color;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.JTextField;



public class tickets extends javax.swing.JFrame {

    public String ST;
    public String ED;
    public String hr;
    public String name;
    public String pp;
    public double discount=0.20;
    public String ID;

    public tickets() {

        initComponents();
        Showmovie();
        showall();

        seat.setEditable(false);
        GCASH.setVisible(false);
        SEAT_PANEL.setVisible(true);
    }

    public void clear(){

        txt_name.setText("");
        ticket_type.resetKeyboardActions();
        customer_date.setDate(null);
        txt_contact.setText("");
        seat.setText("");
        id.setText(ID);
        price.setText(pp);
    }

    public void availabilitseat(){

        if(seat.getText().equals("")){

            av.setVisible(false);
        }

        else{
            av.setVisible(true);
        }
    }

    public void pay(){

        String customername=txt_name.getText();
        String s=((JTextField)customer_date.getDateEditor().getUiComponent()).getText();
        String x = (String)ticket_type.getSelectedItem();
        String availableseat=av.getText().toUpperCase();
        String seatmov=seat.getText().toUpperCase();
        String movie=(String)jComboBox1.getSelectedItem();
       
        if(seat.getText().equals("")|| price.getText().equals("")|| s.equals("")||
           txt_contact.getText().equals("")|| customername.equals("")){

            JOptionPane.showMessageDialog(null, "Fill all fields", "Warning",
                    JOptionPane.WARNING_MESSAGE);

            GCASH.setVisible(false);
            SEAT_PANEL.setVisible(true);
        }

        else if(availableseat.equalsIgnoreCase("Not Available")){

            JOptionPane.showMessageDialog(null, "Seat Already taken", "Warning",
                    JOptionPane.WARNING_MESSAGE);

            GCASH.setVisible(false);
            SEAT_PANEL.setVisible(true);
        }

        else{
            int input = JOptionPane.showConfirmDialog(null, "Movie: "+movie+"\nDuration: "+hr+"\nSeat:  "+seatmov+"\nTicket Type: "+x);

            if(input==(1)){

                clear();
                GCASH.setVisible(false);
                SEAT_PANEL.setVisible(true);
            }

            else if(input==(0)){

                try{

                    long contact=Long.parseLong(txt_contact.getText());
                    int mov_id=Integer.parseInt(id.getText());
                    Double totalamount=Double.parseDouble(price.getText());

                    Class.forName("com.mysql.cj.jdbc.Driver");
                    Connection conn= DriverManager.getConnection("jdbc:mysql://localhost/movie_db","root","");
                    Statement stmt=conn.createStatement();

                    String sql="INSERT INTO `customer`(`name`, `contact`, `ticket_type`, `total_amount`, `seat`, `customer_date` ,`movie_id`) "
                    + "VALUES ('"+customername+"','"+contact+"','"+x+"','"+totalamount+"','"+seatmov+"','"+s+"','"+mov_id+"')";
                    stmt.executeUpdate(sql);

                    JOptionPane.showMessageDialog(null,"Successfully");

                    gc_btn.setText("Gcash");
                    GCASH.setVisible(false);
                    SEAT_PANEL.setVisible(false);
                    showrecord.setVisible(true);

                    jTextArea1.setText(" Please screenshot the details below."
                            +"\n This will serve as your receipt/ ticket to enter"
                            +"\n PROOF OF PURCHASE \n Down Town Ticket "

                            + "\n movie title:        "+movie.toUpperCase()

                            + "\n Duration:           "+hr.toUpperCase()
                            + "\n Date:               "+s.toUpperCase()
                            + "\n Tiket type:         "+x.toUpperCase()
                            + "\n Seat Code:          "+seatmov.toUpperCase()
                            + "\n---------------------------------------------------"
                            + "\n Customer:           "+customername.toUpperCase()
                            + "\n Contact no:          +63 "+contact
                            + "\n Total Price:         ₱"+totalamount
                            + "\n \n*** When discount is availed, please present your ID ");

                    clear();
                }

                catch(Exception e){

                    JOptionPane.showMessageDialog(null, e);
                }
            }

            else{
                GCASH.setVisible(false);
                SEAT_PANEL.setVisible(true);
            }
        }
   }
   
    public void discount_type(){

        String x = ticket_type.getSelectedItem().toString();
        if(x.equalsIgnoreCase("none")){
        txt_discount.setText("No Discount");
        txt_discount.setForeground(new Color(0,153,0));
        price.setText(pp);
        }

        else{

            txt_discount.setText("20%");
            Double tot=Double.parseDouble(pp);
        
            Double total=discount*tot;
            Double disc=tot-total;
            price.setText(disc.toString());
            txt_discount.setForeground(Color.green);
        }
    }

    public void Showmovie(){

        try {

            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn= DriverManager.getConnection("jdbc:mysql://localhost/movie_db","root","");
            Statement st=conn.createStatement();
            String query="SELECT * FROM `movie_table`";
            ResultSet rs=st.executeQuery(query);

            while(rs.next()){

                name=rs.getString("movie_title");
                jComboBox1.addItem(name);
            }
        }

        catch (Exception er) {System.err.println(er);}
    }
  

    public void showall(){

        String x = (String)jComboBox1.getSelectedItem();

        try {

            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn= DriverManager.getConnection("jdbc:mysql://localhost/movie_db","root","");
            Statement st=conn.createStatement();
            String query="SELECT * FROM `movie_table` where movie_title='"+x+"'";
            ResultSet rs=st.executeQuery(query);

            while(rs.next()){

                hr=rs.getString("duration");
                ST=String.valueOf(rs.getDate("start_date"));
                ED=String.valueOf(rs.getDate("end_date"));
                pp=String.valueOf(rs.getInt("price"));
                ID=String.valueOf(rs.getInt("movie_id"));
            }

            id.setText(ID);
            txt_duration.setText(hr);
            start_date.setText(ST);
            end_date.setText(ED);
            price.setText(pp);
        }

        catch (Exception er) {System.err.println(er);}
    }
  
    public void availability(){

        String s=seat.getText();
        String aaa=((JTextField)customer_date.getDateEditor().getUiComponent()).getText();

        try{
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn= DriverManager.getConnection("jdbc:mysql://localhost/movie_db","root","");
            Statement stmt=conn.createStatement();

            PreparedStatement st = (PreparedStatement) conn.prepareStatement("SELECT * FROM customer WHERE movie_id=? AND `seat`=? AND `customer_date`=?");
                    st.setString(1,    id.getText());
                    st.setString(2, s);
                    st.setString(3, aaa);

            ResultSet rs = st.executeQuery();

            if (rs.next()) {

                s=rs.getString(6);
                av.setForeground(Color.red);
                av.setText("Not Available");
            }

            else {

                av.setForeground(new Color(0,153,0));
                av.setText("Available");
            }
        }

        catch(Exception ex){
            JOptionPane.showMessageDialog(null, ex);
        }
    }
  
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel2 = new javax.swing.JPanel();
        jComboBox1 = new javax.swing.JComboBox<>();
        txt_duration = new javax.swing.JLabel();
        start_date = new javax.swing.JLabel();
        end_date = new javax.swing.JLabel();
        price = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        dr = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        ticket_type = new javax.swing.JComboBox<>();
        txt_discount = new javax.swing.JLabel();
        txt_discount1 = new javax.swing.JLabel();
        txt_discount2 = new javax.swing.JLabel();
        dr1 = new javax.swing.JLabel();
        gc_btn = new javax.swing.JButton();
        pay_btn = new javax.swing.JButton();
        txt_name = new javax.swing.JTextField();
        txt_contact = new javax.swing.JTextField();
        id = new javax.swing.JLabel();
        customer_date = new com.toedter.calendar.JDateChooser();
        pay_btn1 = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        SEAT_PANEL = new javax.swing.JPanel();
        jLabel35 = new javax.swing.JLabel();
        a1 = new javax.swing.JButton();
        a2 = new javax.swing.JButton();
        b2 = new javax.swing.JButton();
        b1 = new javax.swing.JButton();
        c2 = new javax.swing.JButton();
        c1 = new javax.swing.JButton();
        a3 = new javax.swing.JButton();
        a4 = new javax.swing.JButton();
        b3 = new javax.swing.JButton();
        b4 = new javax.swing.JButton();
        c3 = new javax.swing.JButton();
        c4 = new javax.swing.JButton();
        c8 = new javax.swing.JButton();
        c7 = new javax.swing.JButton();
        b8 = new javax.swing.JButton();
        b7 = new javax.swing.JButton();
        a8 = new javax.swing.JButton();
        a7 = new javax.swing.JButton();
        a5 = new javax.swing.JButton();
        a6 = new javax.swing.JButton();
        b5 = new javax.swing.JButton();
        b6 = new javax.swing.JButton();
        c5 = new javax.swing.JButton();
        c6 = new javax.swing.JButton();
        av = new javax.swing.JLabel();
        seat = new javax.swing.JTextField();
        GCASH = new javax.swing.JPanel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        btn_insert1 = new javax.swing.JButton();
        showrecord = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();
        btn_showrec = new javax.swing.JButton();
        jLabel6 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setUndecorated(true);

        jComboBox1.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jComboBox1.setBorder(javax.swing.BorderFactory.createTitledBorder("Movie Title"));
        jComboBox1.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jComboBox1ItemStateChanged(evt);
            }
        });
        jComboBox1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox1ActionPerformed(evt);
            }
        });

        txt_duration.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        txt_duration.setForeground(new java.awt.Color(0, 204, 0));
        txt_duration.setText("Duration");

        start_date.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        start_date.setText("yyyy-MM-DD");

        end_date.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        end_date.setText("yyyy-MM-DD");

        price.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        price.setText("Price");

        jLabel1.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 51, 51));
        jLabel1.setText("Until");

        dr.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        dr.setText("Movie Duration:");

        jLabel2.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel2.setText("₱");

        jLabel3.setText("Start date of showing");

        jLabel4.setText("Last date of showing");

        ticket_type.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        ticket_type.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "None", "Student", "Senior Citizen", "Child" }));
        ticket_type.setBorder(javax.swing.BorderFactory.createTitledBorder("Ticket Type"));
        ticket_type.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                ticket_typeItemStateChanged(evt);
            }
        });
        ticket_type.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ticket_typeActionPerformed(evt);
            }
        });

        txt_discount.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        txt_discount.setText("%Discount");

        txt_discount1.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        txt_discount1.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        txt_discount1.setText("Discount: ");

        txt_discount2.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        txt_discount2.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        txt_discount2.setText("Total Amount");

        dr1.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        dr1.setText("Select Date:");

        gc_btn.setBackground(new java.awt.Color(102, 153, 255));
        gc_btn.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        gc_btn.setForeground(new java.awt.Color(255, 255, 255));
        gc_btn.setText("Gcash");
        gc_btn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                gc_btnActionPerformed(evt);
            }
        });

        pay_btn.setBackground(new java.awt.Color(51, 0, 0));
        pay_btn.setForeground(new java.awt.Color(255, 255, 255));
        pay_btn.setText("Pay");
        pay_btn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pay_btnActionPerformed(evt);
            }
        });

        txt_name.setBackground(new java.awt.Color(240, 240, 240));
        txt_name.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        txt_name.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txt_name.setBorder(javax.swing.BorderFactory.createTitledBorder("Full Name"));
        txt_name.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txt_nameActionPerformed(evt);
            }
        });

        txt_contact.setBackground(new java.awt.Color(240, 240, 240));
        txt_contact.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        txt_contact.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txt_contact.setBorder(javax.swing.BorderFactory.createTitledBorder("Contact number"));

        id.setText("ID");

        customer_date.setBackground(new java.awt.Color(255, 255, 255));
        customer_date.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        customer_date.setDateFormatString("y-MM-d");
        customer_date.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseExited(java.awt.event.MouseEvent evt) {
                customer_dateMouseExited(evt);
            }
        });
        customer_date.addInputMethodListener(new java.awt.event.InputMethodListener() {
            public void caretPositionChanged(java.awt.event.InputMethodEvent evt) {
                customer_dateCaretPositionChanged(evt);
            }
            public void inputMethodTextChanged(java.awt.event.InputMethodEvent evt) {
                customer_dateInputMethodTextChanged(evt);
            }
        });
        customer_date.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                customer_datePropertyChange(evt);
            }
        });

        pay_btn1.setBackground(new java.awt.Color(0, 0, 51));
        pay_btn1.setForeground(new java.awt.Color(255, 255, 255));
        pay_btn1.setText("Back");
        pay_btn1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pay_btn1ActionPerformed(evt);
            }
        });

        jPanel3.setBackground(new java.awt.Color(51, 0, 51));
        jPanel3.setLayout(new java.awt.CardLayout());

        SEAT_PANEL.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        jLabel35.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        jLabel35.setForeground(new java.awt.Color(102, 0, 0));
        jLabel35.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel35.setText("SEAT LIST");

        a1.setBackground(new java.awt.Color(204, 204, 204));
        a1.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        a1.setText("A1");
        a1.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(51, 0, 0), 2, true));
        a1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                a1ActionPerformed(evt);
            }
        });

        a2.setBackground(new java.awt.Color(204, 204, 204));
        a2.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        a2.setText("A2");
        a2.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(51, 0, 0), 2, true));
        a2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                a2ActionPerformed(evt);
            }
        });

        b2.setBackground(new java.awt.Color(204, 204, 204));
        b2.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        b2.setText("B2");
        b2.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(51, 0, 0), 2, true));
        b2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                b2ActionPerformed(evt);
            }
        });

        b1.setBackground(new java.awt.Color(204, 204, 204));
        b1.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        b1.setText("B1");
        b1.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(51, 0, 0), 2, true));
        b1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                b1ActionPerformed(evt);
            }
        });

        c2.setBackground(new java.awt.Color(204, 204, 204));
        c2.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        c2.setText("C2");
        c2.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(51, 0, 0), 2, true));
        c2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                c2ActionPerformed(evt);
            }
        });

        c1.setBackground(new java.awt.Color(204, 204, 204));
        c1.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        c1.setText("C1");
        c1.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(51, 0, 0), 2, true));
        c1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                c1ActionPerformed(evt);
            }
        });

        a3.setBackground(new java.awt.Color(204, 204, 204));
        a3.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        a3.setText("A3");
        a3.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(51, 0, 0), 2, true));
        a3.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                a3MouseClicked(evt);
            }
        });
        a3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                a3ActionPerformed(evt);
            }
        });

        a4.setBackground(new java.awt.Color(204, 204, 204));
        a4.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        a4.setText("A4");
        a4.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(51, 0, 0), 2, true));
        a4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                a4ActionPerformed(evt);
            }
        });

        b3.setBackground(new java.awt.Color(204, 204, 204));
        b3.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        b3.setText("B3");
        b3.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(51, 0, 0), 2, true));
        b3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                b3ActionPerformed(evt);
            }
        });

        b4.setBackground(new java.awt.Color(204, 204, 204));
        b4.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        b4.setText("B4");
        b4.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(51, 0, 0), 2, true));
        b4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                b4ActionPerformed(evt);
            }
        });

        c3.setBackground(new java.awt.Color(204, 204, 204));
        c3.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        c3.setText("C3");
        c3.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(51, 0, 0), 2, true));
        c3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                c3ActionPerformed(evt);
            }
        });

        c4.setBackground(new java.awt.Color(204, 204, 204));
        c4.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        c4.setText("C4");
        c4.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(51, 0, 0), 2, true));
        c4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                c4ActionPerformed(evt);
            }
        });

        c8.setBackground(new java.awt.Color(204, 204, 204));
        c8.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        c8.setText("C8");
        c8.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(51, 0, 0), 2, true));
        c8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                c8ActionPerformed(evt);
            }
        });

        c7.setBackground(new java.awt.Color(204, 204, 204));
        c7.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        c7.setText("C7");
        c7.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(51, 0, 0), 2, true));
        c7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                c7ActionPerformed(evt);
            }
        });

        b8.setBackground(new java.awt.Color(204, 204, 204));
        b8.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        b8.setText("B8");
        b8.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(51, 0, 0), 2, true));
        b8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                b8ActionPerformed(evt);
            }
        });

        b7.setBackground(new java.awt.Color(204, 204, 204));
        b7.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        b7.setText("B7");
        b7.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(51, 0, 0), 2, true));
        b7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                b7ActionPerformed(evt);
            }
        });

        a8.setBackground(new java.awt.Color(204, 204, 204));
        a8.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        a8.setText("A8");
        a8.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(51, 0, 0), 2, true));
        a8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                a8ActionPerformed(evt);
            }
        });

        a7.setBackground(new java.awt.Color(204, 204, 204));
        a7.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        a7.setText("A7");
        a7.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(51, 0, 0), 2, true));
        a7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                a7ActionPerformed(evt);
            }
        });

        a5.setBackground(new java.awt.Color(204, 204, 204));
        a5.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        a5.setText("A5");
        a5.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(51, 0, 0), 2, true));
        a5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                a5ActionPerformed(evt);
            }
        });

        a6.setBackground(new java.awt.Color(204, 204, 204));
        a6.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        a6.setText("A6");
        a6.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(51, 0, 0), 2, true));
        a6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                a6ActionPerformed(evt);
            }
        });

        b5.setBackground(new java.awt.Color(204, 204, 204));
        b5.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        b5.setText("B5");
        b5.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(51, 0, 0), 2, true));
        b5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                b5ActionPerformed(evt);
            }
        });

        b6.setBackground(new java.awt.Color(204, 204, 204));
        b6.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        b6.setText("B6");
        b6.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(51, 0, 0), 2, true));
        b6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                b6ActionPerformed(evt);
            }
        });

        c5.setBackground(new java.awt.Color(204, 204, 204));
        c5.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        c5.setText("C5");
        c5.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(51, 0, 0), 2, true));
        c5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                c5ActionPerformed(evt);
            }
        });

        c6.setBackground(new java.awt.Color(204, 204, 204));
        c6.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        c6.setText("C6");
        c6.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(51, 0, 0), 2, true));
        c6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                c6ActionPerformed(evt);
            }
        });

        av.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        av.setForeground(new java.awt.Color(51, 0, 0));
        av.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        av.setText("Seat Availability");

        seat.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        seat.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        seat.addInputMethodListener(new java.awt.event.InputMethodListener() {
            public void caretPositionChanged(java.awt.event.InputMethodEvent evt) {
            }
            public void inputMethodTextChanged(java.awt.event.InputMethodEvent evt) {
                seatInputMethodTextChanged(evt);
            }
        });
        seat.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                seatActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout SEAT_PANELLayout = new javax.swing.GroupLayout(SEAT_PANEL);
        SEAT_PANEL.setLayout(SEAT_PANELLayout);
        SEAT_PANELLayout.setHorizontalGroup(
            SEAT_PANELLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(SEAT_PANELLayout.createSequentialGroup()
                .addGroup(SEAT_PANELLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(SEAT_PANELLayout.createSequentialGroup()
                        .addGap(164, 164, 164)
                        .addComponent(seat, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(SEAT_PANELLayout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(SEAT_PANELLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(av, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(SEAT_PANELLayout.createSequentialGroup()
                                .addGroup(SEAT_PANELLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, SEAT_PANELLayout.createSequentialGroup()
                                        .addComponent(a3, javax.swing.GroupLayout.PREFERRED_SIZE, 59, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(a4, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, SEAT_PANELLayout.createSequentialGroup()
                                        .addComponent(a1, javax.swing.GroupLayout.PREFERRED_SIZE, 59, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(a2, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(SEAT_PANELLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, SEAT_PANELLayout.createSequentialGroup()
                                        .addComponent(b3, javax.swing.GroupLayout.PREFERRED_SIZE, 59, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(b4, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, SEAT_PANELLayout.createSequentialGroup()
                                        .addComponent(b1, javax.swing.GroupLayout.PREFERRED_SIZE, 59, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(b2, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 34, Short.MAX_VALUE)
                                .addGroup(SEAT_PANELLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addGroup(SEAT_PANELLayout.createSequentialGroup()
                                        .addComponent(c3, javax.swing.GroupLayout.PREFERRED_SIZE, 59, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(c4, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(SEAT_PANELLayout.createSequentialGroup()
                                        .addComponent(c1, javax.swing.GroupLayout.PREFERRED_SIZE, 59, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(c2, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE))))
                            .addComponent(jLabel35, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(SEAT_PANELLayout.createSequentialGroup()
                                .addGroup(SEAT_PANELLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(SEAT_PANELLayout.createSequentialGroup()
                                        .addComponent(a7, javax.swing.GroupLayout.PREFERRED_SIZE, 59, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(a8, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(SEAT_PANELLayout.createSequentialGroup()
                                        .addComponent(a5, javax.swing.GroupLayout.PREFERRED_SIZE, 59, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(a6, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(SEAT_PANELLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(SEAT_PANELLayout.createSequentialGroup()
                                        .addComponent(b5, javax.swing.GroupLayout.PREFERRED_SIZE, 59, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(b6, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(SEAT_PANELLayout.createSequentialGroup()
                                        .addComponent(b7, javax.swing.GroupLayout.PREFERRED_SIZE, 59, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(b8, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGroup(SEAT_PANELLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, SEAT_PANELLayout.createSequentialGroup()
                                        .addComponent(c7, javax.swing.GroupLayout.PREFERRED_SIZE, 59, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(c8, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, SEAT_PANELLayout.createSequentialGroup()
                                        .addComponent(c5, javax.swing.GroupLayout.PREFERRED_SIZE, 59, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(c6, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE)))))))
                .addContainerGap())
        );
        SEAT_PANELLayout.setVerticalGroup(
            SEAT_PANELLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(SEAT_PANELLayout.createSequentialGroup()
                .addGap(56, 56, 56)
                .addGroup(SEAT_PANELLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(a1, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(a2, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(c1, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(c2, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(b1, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(b2, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(SEAT_PANELLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(a3, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(a4, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(c3, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(c4, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(b3, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(b4, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(SEAT_PANELLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(a5, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(a6, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(c5, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(c6, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(b5, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(b6, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(SEAT_PANELLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(a7, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(a8, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(c7, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(c8, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(b7, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(b8, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(jLabel35, javax.swing.GroupLayout.PREFERRED_SIZE, 56, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(seat, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(av, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel3.add(SEAT_PANEL, "card2");

        jLabel7.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel7.setIcon(new javax.swing.ImageIcon(getClass().getResource("/movie_system_version5/images/qr_code_PNG3.png"))); // NOI18N

        jLabel8.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        jLabel8.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel8.setIcon(new javax.swing.ImageIcon(getClass().getResource("/movie_system_version5/images/gcash_50px.png"))); // NOI18N
        jLabel8.setText(" Scan Me");

        btn_insert1.setBackground(new java.awt.Color(255, 255, 255));
        btn_insert1.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        btn_insert1.setText("Done");
        btn_insert1.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        btn_insert1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_insert1btn_insertActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout GCASHLayout = new javax.swing.GroupLayout(GCASH);
        GCASH.setLayout(GCASHLayout);
        GCASHLayout.setHorizontalGroup(
            GCASHLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(GCASHLayout.createSequentialGroup()
                .addGap(85, 85, 85)
                .addGroup(GCASHLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel8, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, GCASHLayout.createSequentialGroup()
                        .addComponent(btn_insert1, javax.swing.GroupLayout.PREFERRED_SIZE, 131, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(57, 57, 57)))
                .addContainerGap(114, Short.MAX_VALUE))
        );
        GCASHLayout.setVerticalGroup(
            GCASHLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(GCASHLayout.createSequentialGroup()
                .addGap(27, 27, 27)
                .addComponent(jLabel7)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel8)
                .addGap(65, 65, 65)
                .addComponent(btn_insert1, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(72, Short.MAX_VALUE))
        );

        jPanel3.add(GCASH, "card3");

        showrecord.setBackground(new java.awt.Color(255, 255, 255));

        jTextArea1.setColumns(20);
        jTextArea1.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jTextArea1.setRows(5);
        jScrollPane1.setViewportView(jTextArea1);

        btn_showrec.setBackground(new java.awt.Color(51, 0, 102));
        btn_showrec.setForeground(new java.awt.Color(255, 255, 255));
        btn_showrec.setText("View Seat");
        btn_showrec.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_showrecActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout showrecordLayout = new javax.swing.GroupLayout(showrecord);
        showrecord.setLayout(showrecordLayout);
        showrecordLayout.setHorizontalGroup(
            showrecordLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, showrecordLayout.createSequentialGroup()
                .addContainerGap(184, Short.MAX_VALUE)
                .addComponent(btn_showrec, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(163, 163, 163))
            .addGroup(showrecordLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1)
                .addContainerGap())
        );
        showrecordLayout.setVerticalGroup(
            showrecordLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(showrecordLayout.createSequentialGroup()
                .addGap(69, 69, 69)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 346, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 45, Short.MAX_VALUE)
                .addComponent(btn_showrec, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(22, 22, 22))
        );

        jPanel3.add(showrecord, "card4");

        jLabel6.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        jLabel6.setText("*To avoid error when entering the cinema, please choose date within the given range.");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(32, 32, 32)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(txt_discount1, javax.swing.GroupLayout.PREFERRED_SIZE, 88, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel2Layout.createSequentialGroup()
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(txt_discount, javax.swing.GroupLayout.PREFERRED_SIZE, 138, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(txt_discount2, javax.swing.GroupLayout.PREFERRED_SIZE, 112, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(jLabel2)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(price, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(85, 85, 85))))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel2Layout.createSequentialGroup()
                                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                                .addGroup(jPanel2Layout.createSequentialGroup()
                                                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                    .addGap(40, 40, 40)
                                                    .addComponent(end_date, javax.swing.GroupLayout.PREFERRED_SIZE, 125, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                                    .addComponent(jComboBox1, javax.swing.GroupLayout.Alignment.TRAILING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                    .addGroup(jPanel2Layout.createSequentialGroup()
                                                        .addGap(45, 45, 45)
                                                        .addComponent(dr1, javax.swing.GroupLayout.PREFERRED_SIZE, 108, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                        .addComponent(customer_date, javax.swing.GroupLayout.PREFERRED_SIZE, 157, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                    .addGroup(jPanel2Layout.createSequentialGroup()
                                                        .addComponent(gc_btn, javax.swing.GroupLayout.PREFERRED_SIZE, 106, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                        .addComponent(pay_btn, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                        .addComponent(pay_btn1, javax.swing.GroupLayout.PREFERRED_SIZE, 76, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                    .addComponent(txt_name)
                                                    .addComponent(txt_contact)
                                                    .addComponent(ticket_type, javax.swing.GroupLayout.PREFERRED_SIZE, 368, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                            .addComponent(jLabel6))
                                        .addGap(0, 0, Short.MAX_VALUE))
                                    .addGroup(jPanel2Layout.createSequentialGroup()
                                        .addGap(243, 243, 243)
                                        .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED))))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGap(53, 53, 53)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(start_date, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel3)))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGap(79, 79, 79)
                                .addComponent(dr, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(txt_duration, javax.swing.GroupLayout.PREFERRED_SIZE, 148, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 94, Short.MAX_VALUE)))
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(34, 34, 34))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                        .addComponent(id, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(57, 57, 57))))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addGap(60, 60, 60)
                .addComponent(txt_name, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txt_contact, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txt_duration)
                    .addComponent(dr))
                .addGap(23, 23, 23)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(jLabel4))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(start_date)
                    .addComponent(jLabel1)
                    .addComponent(end_date))
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(dr1)
                    .addComponent(customer_date, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel6)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(ticket_type, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txt_discount1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(txt_discount2, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(txt_discount, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(price)
                    .addComponent(jLabel2))
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(pay_btn, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(gc_btn, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(pay_btn1, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(20, 20, 20))
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(75, 75, 75)
                .addComponent(id)
                .addGap(18, 18, 18)
                .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(34, 34, 34))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 4, Short.MAX_VALUE))
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void btn_showrecActionPerformed(java.awt.event.ActionEvent evt) {
        GCASH.setVisible(false);
        SEAT_PANEL.setVisible(true);
        showrecord.setVisible(false);
    }

    private void btn_insert1btn_insertActionPerformed(java.awt.event.ActionEvent evt) {
        pay();
    }

    private void seatActionPerformed(java.awt.event.ActionEvent evt) {
    }

    private void seatInputMethodTextChanged(java.awt.event.InputMethodEvent evt) {
        availabilitseat();
    }

    private void c6ActionPerformed(java.awt.event.ActionEvent evt) {
        seat.setText(c6.getText());
        availability();
    }

    private void c5ActionPerformed(java.awt.event.ActionEvent evt) {
        seat.setText(c5.getText());
        availability();
    }

    private void b6ActionPerformed(java.awt.event.ActionEvent evt) {
        seat.setText(b6.getText());
        availability();
    }

    private void b5ActionPerformed(java.awt.event.ActionEvent evt) {
        seat.setText(b5.getText());
        availability();
    }

    private void a6ActionPerformed(java.awt.event.ActionEvent evt) {
        seat.setText(a6.getText());
        availability();
    }

    private void a5ActionPerformed(java.awt.event.ActionEvent evt) {
        seat.setText(a5.getText());
        availability();
    }

    private void a7ActionPerformed(java.awt.event.ActionEvent evt) {
        seat.setText(a7.getText());
        availability();
    }

    private void a8ActionPerformed(java.awt.event.ActionEvent evt) {
        seat.setText(a8.getText());
        availability();
    }

    private void b7ActionPerformed(java.awt.event.ActionEvent evt) {
        seat.setText(b7.getText());
        availability();
    }

    private void b8ActionPerformed(java.awt.event.ActionEvent evt) {
        seat.setText(b8.getText());
        availability();
    }

    private void c7ActionPerformed(java.awt.event.ActionEvent evt) {
        seat.setText(c7.getText());
        availability();
    }

    private void c8ActionPerformed(java.awt.event.ActionEvent evt) {
        seat.setText(c8.getText());
        availability();
    }

    private void c4ActionPerformed(java.awt.event.ActionEvent evt) {
        seat.setText(c4.getText());
        availability();
    }

    private void c3ActionPerformed(java.awt.event.ActionEvent evt) {
        seat.setText(c3.getText());
        availability();
    }

    private void b4ActionPerformed(java.awt.event.ActionEvent evt) {
        seat.setText(b4.getText());
        availability();
    }

    private void b3ActionPerformed(java.awt.event.ActionEvent evt) {
        seat.setText(b3.getText());
        availability();
    }

    private void a4ActionPerformed(java.awt.event.ActionEvent evt) {
        seat.setText(a4.getText());
        availability();
    }

    private void a3ActionPerformed(java.awt.event.ActionEvent evt) {
        seat.setText(a3.getText());
        availability();
    }

    private void a3MouseClicked(java.awt.event.MouseEvent evt) {
        seat.setText(a3.getText());
    }

    private void c1ActionPerformed(java.awt.event.ActionEvent evt) {
        seat.setText(c1.getText());
        availability();
    }

    private void c2ActionPerformed(java.awt.event.ActionEvent evt) {
        seat.setText(c2.getText());
        availability();
    }

    private void b1ActionPerformed(java.awt.event.ActionEvent evt) {
        seat.setText(b1.getText());
        availability();
    }

    private void b2ActionPerformed(java.awt.event.ActionEvent evt) {
        seat.setText(b2.getText());
        availability();
    }

    private void a2ActionPerformed(java.awt.event.ActionEvent evt) {
        seat.setText(a2.getText());
        availability();
    }

    private void a1ActionPerformed(java.awt.event.ActionEvent evt) {

        seat.setText(a1.getText());
        availability();

    }

    private void pay_btn1ActionPerformed(java.awt.event.ActionEvent evt) {

        Homepage hp=new  Homepage();
        hp.setVisible(true);
        this.setVisible(false);
    }

    private void customer_datePropertyChange(java.beans.PropertyChangeEvent evt) {
        availability();
    }

    private void customer_dateInputMethodTextChanged(java.awt.event.InputMethodEvent evt) {
        JOptionPane.showMessageDialog(null,"asdasd");
    }

    private void customer_dateCaretPositionChanged(java.awt.event.InputMethodEvent evt) {
        seat.setText("");
        av.setText("Seat Availability");
    }

    private void customer_dateMouseExited(java.awt.event.MouseEvent evt) {
        ;
    }

    private void pay_btnActionPerformed(java.awt.event.ActionEvent evt) {

        GCASH.setVisible(false);
        SEAT_PANEL.setVisible(true);

        pay();
    }

    private void gc_btnActionPerformed(java.awt.event.ActionEvent evt) {

        GCASH.setVisible(true);
        SEAT_PANEL.setVisible(false);
    }

    private void ticket_typeActionPerformed(java.awt.event.ActionEvent evt) {
    }

    private void ticket_typeItemStateChanged(java.awt.event.ItemEvent evt) {
        discount_type();
    }

    private void jComboBox1ActionPerformed(java.awt.event.ActionEvent evt) {
    }

    private void jComboBox1ItemStateChanged(java.awt.event.ItemEvent evt) {
        showall();
        availability();
    }

    private void txt_nameActionPerformed(java.awt.event.ActionEvent evt) {
    }


    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(tickets.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(tickets.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(tickets.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(tickets.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new tickets().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel GCASH;
    private javax.swing.JPanel SEAT_PANEL;
    private javax.swing.JButton a1;
    private javax.swing.JButton a2;
    private javax.swing.JButton a3;
    private javax.swing.JButton a4;
    private javax.swing.JButton a5;
    private javax.swing.JButton a6;
    private javax.swing.JButton a7;
    private javax.swing.JButton a8;
    private javax.swing.JLabel av;
    private javax.swing.JButton b1;
    private javax.swing.JButton b2;
    private javax.swing.JButton b3;
    private javax.swing.JButton b4;
    private javax.swing.JButton b5;
    private javax.swing.JButton b6;
    private javax.swing.JButton b7;
    private javax.swing.JButton b8;
    private javax.swing.JButton btn_insert1;
    private javax.swing.JButton btn_showrec;
    private javax.swing.JButton c1;
    private javax.swing.JButton c2;
    private javax.swing.JButton c3;
    private javax.swing.JButton c4;
    private javax.swing.JButton c5;
    private javax.swing.JButton c6;
    private javax.swing.JButton c7;
    private javax.swing.JButton c8;
    private com.toedter.calendar.JDateChooser customer_date;
    private javax.swing.JLabel dr;
    private javax.swing.JLabel dr1;
    private javax.swing.JLabel end_date;
    private javax.swing.JButton gc_btn;
    private javax.swing.JLabel id;
    private javax.swing.JComboBox<String> jComboBox1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel35;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextArea jTextArea1;
    private javax.swing.JButton pay_btn;
    private javax.swing.JButton pay_btn1;
    private javax.swing.JLabel price;
    private javax.swing.JTextField seat;
    private javax.swing.JPanel showrecord;
    private javax.swing.JLabel start_date;
    private javax.swing.JComboBox<String> ticket_type;
    private javax.swing.JTextField txt_contact;
    private javax.swing.JLabel txt_discount;
    private javax.swing.JLabel txt_discount1;
    private javax.swing.JLabel txt_discount2;
    private javax.swing.JLabel txt_duration;
    private javax.swing.JTextField txt_name;
    // End of variables declaration//GEN-END:variables
}
