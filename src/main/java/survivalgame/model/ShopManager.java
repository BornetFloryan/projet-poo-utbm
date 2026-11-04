package survivalgame.model;

/** Vérifie et applique les achats entre deux vagues. */
public final class ShopManager {
    public enum Item { SMG, RIFLE, DAMAGE, RATE, HEAL }

    public String buy(GameState state, Item item) {
        Player player = state.getPlayer();
        int price = priceOf(item);
        if (player.getCredits() < price) return "Crédits insuffisants.";
        if (item == Item.HEAL && player.getHealth() == player.getMaxHealth()) {
            return "Vie déjà au maximum.";
        }
        player.spendCredits(price);
        applyPurchase(player, item);
        return "Achat effectué.";
    }

    private int priceOf(Item item) {
        return switch (item) {
            case SMG -> 35;
            case RIFLE -> 55;
            case DAMAGE, RATE -> 30;
            case HEAL -> 25;
        };
    }

    private void applyPurchase(Player player, Item item) {
        Weapon current = player.getWeapon();
        switch (item) {
            case SMG -> player.setWeapon(Weapon.smg());
            case RIFLE -> player.setWeapon(Weapon.rifle());
            case DAMAGE -> player.setWeapon(new Weapon(current.name(),
                    Math.min(50, current.damage() + 4), current.cooldownMs()));
            case RATE -> player.setWeapon(new Weapon(current.name(), current.damage(),
                    Math.max(70, current.cooldownMs() - 35)));
            case HEAL -> player.healPlayer(40);
        }
    }
}
