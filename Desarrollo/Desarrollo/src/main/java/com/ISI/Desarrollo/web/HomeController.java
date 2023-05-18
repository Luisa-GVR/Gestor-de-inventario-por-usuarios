package com.ISI.Desarrollo.web;

import com.ISI.Desarrollo.Database.Methods;
import com.ISI.Desarrollo.Database.MyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.sql.SQLException;
import java.util.List;

@Controller
public class HomeController {

    @Autowired
    MyService myService;

    @RequestMapping("/login")
    public String login() {

        return "login";
    }

    @RequestMapping("/home")
    public String home(Model model) throws SQLException {
        Methods metodos = new Methods(myService);

        //Dislpay name for the user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        model.addAttribute("username", authentication.getName());
        //Display database
        model.addAttribute("database", metodos.showDatabase());

        return "home";
    }


    @RequestMapping("/delete/**")
    public String admin(){

        return "delete";
    }
    @RequestMapping("/submitDelete")
    public String deletesubmit(@RequestParam("Codigo")int codigo, @RequestParam("actperformed") String confirmacion){

        Methods metodos = new Methods(myService);
        boolean actperformed = Boolean.parseBoolean(confirmacion);

        if (actperformed) {
            metodos.deleteAttribute(codigo);
        }

        return "redirect:/home";
    }

    @RequestMapping("/add")
    public String add()  {

        return "add";
    }


    @PostMapping("/submitAdd")
    public String submitAdd(@RequestParam("Codigo") int codigo,
                            @RequestParam("Producto") String producto,
                            @RequestParam("Cantidad") int cantidad,
                            @RequestParam("Descripcion") String descripcion,
                            @RequestParam("Precio") int precio){


        Methods metodos = new Methods(myService);
        metodos.addAttribute(codigo, producto, cantidad, descripcion, precio);

        return "redirect:/home";
    }

    @RequestMapping("/edit/**")
    public String edit(Model model) throws SQLException {
        Methods metodos = new Methods(myService);
        List<String> nombres = metodos.productoNames();


        model.addAttribute("nombresProducto", nombres);

        return "edit";
    }


    @PostMapping("/edit/change")
    public String hacerCambios(@RequestParam("producto") String producto, Model model){
        Methods metodos = new Methods(myService);
        String debug = String.valueOf(producto);

        int [] valores = metodos.valores(producto);
        model.addAttribute("valorCantidad", valores[0]);
        model.addAttribute("valorPrecio",valores[1]);
        model.addAttribute("ID", debug);



        return "cambios";
    }

    @PostMapping("/submitEdit")
    public String editsubmit( @RequestParam("Cantidad") int cantidad,
                              @RequestParam("Precio") int precio,
                              @RequestParam("ID") String producto, Model model){
        Methods metodos = new Methods(myService);
        metodos.editAttribute(cantidad, precio, producto);

        return "redirect:/home";
    }

}