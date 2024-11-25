package com.proempresa.campaniamodule.service.impl;

import com.proempresa.campaniamodule.client.*;
import com.proempresa.campaniamodule.config.ResPropertiesConfig;
import com.proempresa.campaniamodule.model.entity.Campania;
import com.proempresa.campaniamodule.model.entity.Colaborador;
import com.proempresa.campaniamodule.model.request.*;
import com.proempresa.campaniamodule.model.response.*;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class EnviarNotificacionImpl {
    private final ResPropertiesConfig resPropertiesConfig;
    private final SmsUtilClient sms;
    private final EmailUtilClient email;
    private final WhatsappUtilClient whatsapp;


    public String notificar(Colaborador colaborador, Campania campania) {
        boolean success = false;
        String metodoEnvio = null;

        // Primero intenta con el método preferencial
        if ("EMAIL".equalsIgnoreCase(resPropertiesConfig.getEnvioPreferencial())) {
            if (isValidEmail(colaborador.getEmail())) {
                success = sendEmail(colaborador, campania);
                metodoEnvio = "EMAIL";
                //servicioCaido = "EMAIL";
            }
        } else if ("WHATSAPP".equalsIgnoreCase(resPropertiesConfig.getEnvioPreferencial())) {
            if (isValidPhoneNumber(colaborador.getTelefono())) {
                success = sendWhatsApp(colaborador, campania);
                metodoEnvio = "WHATSAPP";
                //servicioCaido = "WHATSAPP";
            }
        } else if ("SMS".equalsIgnoreCase(resPropertiesConfig.getEnvioPreferencial())) {
            if (isValidPhoneNumber(colaborador.getTelefono())) {
                success = sendSMS(colaborador, campania);
                metodoEnvio = "SMS";
                //servicioCaido = "SMS";
            }
        }

        // Si falla el método preferencial, intenta con WhatsApp
        if (!success && isValidPhoneNumber(colaborador.getTelefono())) {
            success = sendWhatsApp(colaborador, campania);
            metodoEnvio = "WHATSAPP";
            //servicioCaido = "EMAIL";
        }

        // Si falla WhatsApp, intenta con SMS
        if (!success && isValidPhoneNumber(colaborador.getTelefono())) {
            success = sendSMS(colaborador, campania);
            metodoEnvio = "SMS";
            //servicioCaido = "WHATSAPP";
        }

        // Si falla SMS, intenta con email
        if (!success && isValidEmail(colaborador.getEmail())) {
            success = sendEmail(colaborador, campania);
            metodoEnvio = "EMAIL";
            //servicioCaido = "SMS";
        }
        return metodoEnvio;
    }

    private boolean isValidEmail(String email) {
        return email != null && !email.trim().isEmpty() && email.contains("@");
    }

    private boolean isValidPhoneNumber(String phoneNumber) {
        return phoneNumber != null && !phoneNumber.trim().isEmpty() && phoneNumber.matches("\\d+");
    }

    private boolean sendSMS(Colaborador colaborador, Campania campania) {
        try {

            //CREAR EL MENSAJE A ENVIAR
            String message = "Estimado colaborador, tiene un nuevo cliente asignado con nombre " + campania.getNomCliente()
                    +  ", y numero de telefono "+ colaborador.getTelefono() + ",por un monto de " + campania.getMonto() + " y una tasa del " + campania.getTasa();

            SmsUtilEnvioRequest smsUtilEnvioRequest = new SmsUtilEnvioRequest();
            smsUtilEnvioRequest.setMessage(message);
            smsUtilEnvioRequest.setPhones(new String[]{colaborador.getTelefono()});
            SmsUtilEnvioResponse smsUtilEnvioResponse = sms.enviar(smsUtilEnvioRequest);
            return smsUtilEnvioResponse.getHttpStatus() == 0 || smsUtilEnvioResponse.getHttpStatus() == 200;
        } catch (Exception e) {
            return false;
        }
    }
    private boolean sendWhatsApp(Colaborador colaborador, Campania campania) {
        try {
            WhatsappUtilRequest whatsappUtilRequest = new WhatsappUtilRequest();
            whatsappUtilRequest.setTemplate(resPropertiesConfig.getTemplate());
            List<String> variables = new ArrayList<>();
            //Aqui se agregan las variables del tempalte, no nulas
            variables.add(campania.getNomCliente());
            variables.add(campania.getTelefono());
            variables.add(campania.getCodCliente());
            whatsappUtilRequest.setVariables(variables);

            whatsappUtilRequest.setLanguage(resPropertiesConfig.getLenguage());
            whatsappUtilRequest.setPhone(colaborador.getTelefono());
            whatsapp.sendMessage(whatsappUtilRequest);
            return true;

        } catch (Exception e) {
            return false;
        }
    }

    private boolean sendEmail(Colaborador colaborador, Campania campania) {
        try {
            String subject = "Campaña " + campania.getCampania();

            //CREAR EL MENSAJE A ENVIAR
            String message = "Estimado colaborador, tiene un nuevo cliente asignado con nombre " + campania.getNomCliente()
                    +  ", y numero de telefono "+ colaborador.getTelefono() + ",por un monto de " + campania.getMonto() + " y una tasa del " + campania.getTasa();

            EmailUtilEnvioRequest emailUtilEnvioRequest = new EmailUtilEnvioRequest();
            emailUtilEnvioRequest.setTo(colaborador.getEmail());
            emailUtilEnvioRequest.setSubject(subject);
            emailUtilEnvioRequest.setBody(message);
            EmailUtilEnvioResponse emailUtilEnvioResponse = email.enviar(emailUtilEnvioRequest);
            return emailUtilEnvioResponse.getHttpStatus() == 0 || emailUtilEnvioResponse.getHttpStatus() == 200;
        } catch (Exception e) {
            return false;
        }
    }

}
