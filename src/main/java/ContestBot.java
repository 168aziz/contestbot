import contestlist.ContestsParse;
import model.Contest;
import model.ContestList;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import static com.vdurmont.emoji.EmojiParser.parseToAliases;
import static com.vdurmont.emoji.EmojiParser.parseToUnicode;
import static java.time.format.DateTimeFormatter.ofPattern;

public class ContestBot extends TelegramLongPollingBot {

    private static final DateTimeFormatter dtformatter = ofPattern("yyyy-MM-dd kk:mm:ss");
    private List<Message> messages;
    private Map<Long, Message> inlineKeyboards;
    private boolean isStarted;

    @Override
    public String getBotToken() {
        return "1285652082:AAFw9RPa000s0A9FyiX-omX7fyjjHy7-CE4";
    }

    public String getBotUsername() {
        return "contestlistbot";
    }


    public void onUpdateReceived(Update update) {
        if (messages == null) messages = new CopyOnWriteArrayList<>();
        if (inlineKeyboards == null) inlineKeyboards = new ConcurrentHashMap<>();
        if (update.hasMessage()) {
            Long chat_id = update.getMessage().getChatId();

            for (Message message : messages) {
                try {
                    if (message.getChatId().equals(chat_id))
                        execute(new DeleteMessage(message.getChatId(), message.getMessageId()));
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            }
            messages.clear();

            Message message = update.getMessage();
            try {
                if (!message.getText().equals("/start")) {
                    DeleteMessage deleteMessage = new DeleteMessage(message.getChatId(), message.getMessageId());
                    execute(deleteMessage);
                }
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
            switch (parseToAliases(message.getText())) {
                case ":spiral_calendar_pad: All":
                    if (inlineKeyboards.get(message.getChatId()) == null) {
                        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
                        List<InlineKeyboardButton> row = new ArrayList<>();
                        List<InlineKeyboardButton> row1 = new ArrayList<>();
                        List<InlineKeyboardButton> row2 = new ArrayList<>();
                        List<InlineKeyboardButton> row3 = new ArrayList<>();

                        row.add(new InlineKeyboardButton(parseToUnicode(":one: Codeforces")).setCallbackData("codeforces"));
                        row.add(new InlineKeyboardButton(parseToUnicode(":two: Codechef")).setCallbackData("code_chef"));
                        row1.add(new InlineKeyboardButton(parseToUnicode(":three: Hackerrank")).setCallbackData("hacker_rank"));
                        row1.add(new InlineKeyboardButton(parseToUnicode(":four: Hackerearth")).setCallbackData("hacker_earth"));
                        row2.add(new InlineKeyboardButton(parseToUnicode(":five: CS Academy")).setCallbackData("cs_academy"));
                        row2.add(new InlineKeyboardButton(parseToUnicode(":six: KickStart")).setCallbackData("kick_start"));
                        row3.add(new InlineKeyboardButton(parseToUnicode(":seven: LeetCode")).setCallbackData("leet_code"));
                        row3.add(new InlineKeyboardButton(parseToUnicode(":eight: Atcoder")).setCallbackData("at_coder"));

                        List<List<InlineKeyboardButton>> buttons = new ArrayList<>();
                        buttons.add(row);
                        buttons.add(row1);
                        buttons.add(row2);
                        buttons.add(row3);
                        inlineKeyboardMarkup.setKeyboard(buttons);

                        SendMessage sendMessage = new SendMessage();
                        sendMessage.setText("Select:");
                        sendMessage.setChatId(message.getChatId());
                        sendMessage.setReplyMarkup(inlineKeyboardMarkup);

                        try {
                            inlineKeyboards.put(message.getChatId(), execute(sendMessage));
                        } catch (TelegramApiException e) {
                            e.printStackTrace();
                        }
                    }
                    break;
                case ":calendar: Today":
                    Set<Contest> contests = ContestsParse.get(ContestList.ALL);
                    if (contests.isEmpty()) sendMessage("Not Found ¯\\_(ツ)_/¯", message.getChatId());
                    else contests.forEach(contest -> {
                        String build = "<b>Name:</b>  " + contest.getName() + "\n" +
                                "<b>URL:</b>  " + contest.getUrl() + "\n" +
                                "<b>Start:</b>  " + dtformatter.format(contest.getStart()) + "\n" +
                                "<b>End:</b>  " + dtformatter.format(contest.getEnd()) + "\n";
                        sendMessage(build, message.getChatId());
                    });

                    break;
                case "/start":
                    ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
                    replyKeyboardMarkup.setResizeKeyboard(true);
                    replyKeyboardMarkup.setOneTimeKeyboard(true);

                    List<KeyboardRow> row_list = new ArrayList<>();
                    KeyboardRow row = new KeyboardRow();
                    row.add(new KeyboardButton(parseToUnicode(":calendar:") + " Today"));
                    row.add(new KeyboardButton(parseToUnicode(":spiral_calendar_pad:") + " All"));

                    row_list.add(row);
                    replyKeyboardMarkup.setKeyboard(row_list);

                    SendMessage sendMessage = new SendMessage();
                    sendMessage.setReplyMarkup(replyKeyboardMarkup);
                    sendMessage.setChatId(message.getChatId());
                    sendMessage.setText("Choose: ");

                    try {
                        messages.add(execute(sendMessage));
                    } catch (TelegramApiException e) {
                        e.printStackTrace();
                    }
                    break;
                case "/clear":
                    messages.forEach(msg -> {
                        try {
                            execute(new DeleteMessage(msg.getChatId(), msg.getMessageId()));
                        } catch (TelegramApiException e) {
                            e.printStackTrace();
                        }
                    });
                    messages.clear();
                    Message msg = inlineKeyboards.get(message.getChatId());
                    if (msg != null) {
                        try {
                            execute(new DeleteMessage(msg.getChatId(), msg.getMessageId()));
                        } catch (TelegramApiException e) {
                            e.printStackTrace();
                        }
                        inlineKeyboards.remove(message.getChatId());
                    }
                    break;
                case "/about":
                    String build = parseToUnicode(":man_technologist:") + " <b>Developer: </b> Tuychiyev Azizbek\n" +
                            parseToUnicode(":inbox_tray:") + " <b>Telegram: </b> @aziz_168\n";
                    sendMessage(build, message.getChatId());
                    break;
            }
        } else if (update.hasCallbackQuery()) {
            CallbackQuery callbackQuery = update.getCallbackQuery();
            Long chat_id = callbackQuery.getMessage().getChatId();
            try {
                execute(new DeleteMessage(chat_id, inlineKeyboards.get(chat_id).getMessageId()));
                inlineKeyboards.remove(chat_id);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }

            ContestList contest_type = Arrays.stream(ContestList.values()).filter(c -> c.getName().equals(callbackQuery.getData())).findFirst().get();

            Set<Contest> contests = ContestsParse.get(contest_type);
            if (contests.isEmpty()) sendMessage("Not Found ¯\\_(ツ)_/¯", callbackQuery.getMessage().getChatId());
            else contests.forEach(contest -> {
                String build = "<b>Name:</b>  " + contest.getName() + "\n" +
                        "<b>URL:</b>  " + contest.getUrl() + "\n" +
                        "<b>Start:</b>  " + dtformatter.format(contest.getStart()) + "\n" +
                        "<b>End:</b>  " + dtformatter.format(contest.getEnd()) + "\n";
                sendMessage(build, callbackQuery.getMessage().getChatId());
            });

        }


    }

    private void sendMessage(String build, long chat_id) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setText(build);
        sendMessage.setChatId(chat_id);
        sendMessage.enableHtml(true);
        try {
            messages.add(execute(sendMessage));
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

}
