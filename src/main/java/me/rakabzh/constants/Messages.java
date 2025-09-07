package me.rakabzh.constants;

public enum Messages {

    STARTING_SERVER("§aLe serveur vient de redémarrer !"),
    NO_PLACE_INVENTORY("§4Vous n'avez plus de place dans l'inventaire"),
    IS_IN_PLAYING("Le jeu a déja démarrer !"),
    SAME_ARMOR("§4Vous avez déja une meilleure armure ou la même"),
    SAME_TOOL("§4Vous avez déja cet outils ou un mieux"),
    SAME_SWORD("§4Vous avez déja une meilleure épée ou la même"),
    GET_SHEARS("§4Vous avez déja une cisailles"),
    BED_BREAK_HIMSELF("§cPourquoi se suicider ?"),
    BED_REAL_BREAK("§fLIT DéTRUIT > Votre lit vient d'être cassé par "),
    MUST_IS_IN_BASE("Il faut etre dans la base de quelqu'un"),
    BLOCK_MAP_BREAK("§cVous ne pouvez pas casser les blocks de la map !"),
    GET_SHARPNESS("§4Vous avez déja sharpness"),
    CANT_PAY("§4Vous n'avez pas de quoi payer"),
    MAXIMAL_PROTECTION("§4Vous avez déja le maximun de protection"),
    MAXIMAL_HASTE("§4Vous avez déja le maximun de Haste"),
    MAXIMAL_FORGE("§4Vous avez déja le maximun de Forge"),
    MAXIMAL_TRAP("§4Vous avez le niveaux maximun ou vous ne pouvez pas avoir plus que 3 pièges"),
    GET_REGENERATION("§4Vous avez déja le champs de régénération"),
    GET_BUY("§4Vous avez déja pris cet achat"),
    GET_TRAP("§4Vous avez déja ce piège"),
    GET_ALARM("§4Vous avez déja l'alarme"),

    FAILED_COLOR_TEAM("§cCette couleur d'équipe n'existe pas !"),
    FAILED_SAVE("§cLe ficher ne peux pas se sauvegarder"),

    LOCCATION_IS_NULL("!cLa localisation est null !"),

    COMMAND_INVALID_ARGS_LENGTH("§cLe nombre d'argument ne sont pas valide !"),
    COMMAND_INVALID_ARG("§cL'argument n'est pas valide !"),

    NOT_PLAYER("§cVous n'êtes pas un joueur !");
    private final String message;

    Messages(String message){
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

}
