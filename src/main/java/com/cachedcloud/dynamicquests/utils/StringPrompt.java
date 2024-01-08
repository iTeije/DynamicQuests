package com.cachedcloud.dynamicquests.utils;

import lombok.AllArgsConstructor;
import me.lucko.helper.text3.Text;
import me.lucko.helper.utils.Players;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.ConversationFactory;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.ValidatingPrompt;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

@AllArgsConstructor
public class StringPrompt extends ValidatingPrompt {

  private final String prompt;
  private final Consumer<String> handler;

  @Override
  protected boolean isInputValid(ConversationContext context, String input) {
    return !(input.isBlank());
  }

  @Override
  public @NotNull String getPromptText(ConversationContext context) {
    return Text.colorize(this.prompt);
  }

  @Override
  protected Prompt acceptValidatedInput(ConversationContext context, String input) {
    this.handler.accept(input);
    return Prompt.END_OF_CONVERSATION;
  }

  public static void startPrompt(final Plugin plugin, Player player, String prompt, Consumer<String> responseHandler) {
    new ConversationFactory(plugin)
        .withTimeout(180)
        .withLocalEcho(false)
        .withModality(false)
        .withFirstPrompt(new StringPrompt(prompt, responseHandler))
        .withEscapeSequence("cancel")
        .withEscapeSequence("exit")
        .withEscapeSequence("stop")
        .withEscapeSequence("quit")
        .addConversationAbandonedListener(event -> {
          if (!event.gracefulExit()) {
            Players.msg(player, "&cPrompt cancelled.");
            responseHandler.accept(null);
          }
        })
        .buildConversation(player)
        .begin();
  }

}
