package com.fiap.m4g.clearvolt.domain.controller.web;

import com.fiap.m4g.clearvolt.domain.entity.Dispositivo;
import com.fiap.m4g.clearvolt.domain.entity.Usuario;
import com.fiap.m4g.clearvolt.domain.repository.DispositivoRepository;
import com.fiap.m4g.clearvolt.domain.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.Optional;

@Controller
public class Navigations {
    @Autowired
    UsuarioRepository usuarioRepository;

    @Autowired
    DispositivoRepository dispositivoRepository;


    private Authentication getAuthentication() { return SecurityContextHolder.getContext().getAuthentication(); }
    private boolean autenticado() { return getAuthentication() != null && getAuthentication().isAuthenticated() && !(getAuthentication().getPrincipal() instanceof String && "anonymousUser".equals(getAuthentication().getPrincipal())); }



    @GetMapping("/")
    public ModelAndView homeNull() {
        return new ModelAndView("redirect:/home");
    }

    @GetMapping("/home")
    public ModelAndView pageHome() {
        ModelAndView mv = new ModelAndView("index");
        mv.addObject("autenticado", autenticado());
        return mv;
    }





    @GetMapping("/acesso_negado")
    public ModelAndView pageAcessoNegado() {
        ModelAndView mv = new ModelAndView("acesso_negado");
        mv.addObject("autenticado", autenticado());
        return mv;
    }



}
