package com.weightcalc;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.math.RoundingMode;
import java.math.BigDecimal;
import javax.inject.Inject;
import net.runelite.api.Item;
import net.runelite.api.ItemComposition;
import net.runelite.client.game.ItemManager;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.components.LineComponent;

public class WeightCalcOverlayPanel extends OverlayPanel
{

	private final WeightCalcConfig config;
	private final WeightCalcPlugin plugin;

	@Inject
	private ItemManager itemManager;

	@Inject
	private WeightCalcOverlayPanel(WeightCalcPlugin plugin, WeightCalcConfig config)
	{
		super(plugin);
		this.plugin = plugin;
		this.config = config;
		setLayer(OverlayLayer.ALWAYS_ON_TOP);
	}

	@Override
	public Dimension render(Graphics2D graphics)
	{

		BigDecimal minWeight = WeightCalcPlugin.getMinWeight().setScale(3, RoundingMode.HALF_UP);
		BigDecimal maxWeight = WeightCalcPlugin.getMaxWeight().setScale(3, RoundingMode.HALF_UP);
		BigDecimal aloneWeight = WeightCalcPlugin.getAloneWeightBD();
		int state = plugin.getState();

		if (state == WeightCalcPlugin.STATE_EQUIPPED)
		{
			panelComponent.getChildren().add(LineComponent.builder().left("Remove all equipped items.").build());
		}
		else if (state == WeightCalcPlugin.STATE_TOO_MANY_ITEMS)
		{
			panelComponent.getChildren().add(LineComponent.builder().left("Too many non-weighing items in inventory.").build());
		}
		else if (state == WeightCalcPlugin.STATE_EMPTY)
		{
			panelComponent.getChildren().add(LineComponent.builder().left("Remove all items from your inventory and then add the item to weigh to your inventory.").build());
		}
		else if (state == WeightCalcPlugin.STATE_UNKNOWN)
		{
			panelComponent.getChildren().add(LineComponent.builder().left("Please restart the plugin and try again.").build());
		}
		else
		{
			Item currentItem = WeightCalcPlugin.getCurrentItem();
			ItemComposition item = itemManager.getItemComposition(currentItem.getId());
			panelComponent.getChildren().add(LineComponent.builder().left("Weighing: " + item.getName()).build());
			panelComponent.getChildren().add(LineComponent.builder().left("").build());
			if (WeightCalcPlugin.getWm() != null)
			{
				ItemComposition weighingItem = itemManager.getItemComposition(WeightCalcPlugin.getWm().getItemId());
				String message = (WeightCalcPlugin.getWm().isWithdrawMore() ? "Withdraw " : "Deposit ") + weighingItem.getName();
				panelComponent.getChildren().add(LineComponent.builder().left(message).build());
			}
			if (minWeight.compareTo(maxWeight) == 0)
			{
				panelComponent.getChildren().add(LineComponent.builder().left("Final weight: " + (BigDecimal.ONE.subtract(maxWeight).add(aloneWeight).toString())).build());
			}
			// Might want to add this as a toggle for debugging.
			// else
			// {
			// 	panelComponent.getChildren().add(LineComponent.builder().left("Added weights to try: " + minWeight.toString() + " - " + maxWeight.toString()).build());
			// }
		}


		return super.render(graphics);
	}
}
