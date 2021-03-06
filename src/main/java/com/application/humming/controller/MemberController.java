package com.application.humming.controller;

import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.support.SessionStatus;

import com.application.humming.constant.PageConstants;
import com.application.humming.constant.SessionObjects;
import com.application.humming.dto.MemberDto;
import com.application.humming.dto.OrderDto;
import com.application.humming.entity.MemberEntity;
import com.application.humming.exception.HummingException;
import com.application.humming.form.LoginForm;
import com.application.humming.form.RegistForm;
import com.application.humming.form.WithdrawForm;
import com.application.humming.service.MemberService;
import com.application.humming.service.OrderService;
import com.application.humming.util.PropertiesUtil;

@Controller
@RequestMapping("/member")
public class MemberController {

    @Autowired
    private MemberService memberService;

    @Autowired
    private OrderService orderService;

    @Autowired
    HttpSession session;

    @ModelAttribute
    public RegistForm setUpRegistForm() {
        return new RegistForm();
    }

    @ModelAttribute
    public LoginForm setUpLoginForm() {
        return new LoginForm();
    }

    /** ログインエラーメッセージモデル. */
    private static final String LOGIN_ERROR_MESSAGE_MODEL = "loginErrorMessage";
    /** 会員登録エラーメッセージ（メールアドレス）モデル. */
    private static final String REGIST_ERROR_EMAIL_MESSAGE_MODEL = "registErrorEmailMessage";
    /** 会員登録エラーメッセージ（パスワード）モデル. */
    private static final String REGIST_ERROR_PASSWORD_MESSAGE_MODEL = "registErrorPasswordMessage";
    /** 注文リストモデル. */
    private static final String ORDER_LIST_MODEL = "orderList";
    /** 注文アイテムリストモデル. */
    private static final String ORDER_ITEM_LIST_MODEL = "orderItemList";

    /**
     * ログイン画面を表示する.
     *
     * @return ログイン画面
     */
    @RequestMapping(value = "/login/input")
    public String displayLoginPage() {
        return PageConstants.LOGIN_PAGE;
    }

    /**
     * ログイン処理を行う.
     *
     * @return 初期表示画面
     */
    @RequestMapping(value = "/login/complete")
    public String login(@Validated LoginForm loginForm, BindingResult result, Model model) {
        if (result.hasErrors()) {
            return displayLoginPage();
        }
        final String email = loginForm.getEmail();
        final String password = loginForm.getPassword();

        final MemberDto memberDto = memberService.createMemberDto(email, password);
        if (memberDto.getId() == null) {
            model.addAttribute(LOGIN_ERROR_MESSAGE_MODEL, PropertiesUtil.getProperties("login.error.invalid"));
            return displayLoginPage();
        }
        session.setAttribute(SessionObjects.MEMBER, memberDto);

        // 注文情報がある場合は買い物かごへ遷移
        if ((OrderDto) session.getAttribute(SessionObjects.ORDER) != null) {
            return "redirect:/order/cart";
        }
        return "redirect:/";
    }

    /**
     * ログアウト処理を行う.
     *
     * @return 初期表示画面
     */
    @RequestMapping(value = "/logout")
    public String logout(SessionStatus sessionStatus) {
        memberService.logout(sessionStatus);
        return "redirect:/";
    }

    /**
     * 会員登録入力画面を表示する.
     *
     * @return 会員登録入力画面
     */
    @RequestMapping(value = "/regist/input")
    public String displayRegistInputPage() {
        return PageConstants.REGIST_INPUT_PAGE;
    }

    /**
     * 会員登録の確認を行う.
     *
     * @return 会員登録確認画面
     */
    @RequestMapping(value = "/regist/confirm")
    public String displayRegistConfirmPage(@Validated RegistForm registForm, BindingResult result, Model model) {
        if (result.hasErrors()) {
            return displayRegistInputPage();
        }

        final String email = registForm.getEmail();
        final boolean isUniqueEmailFlag = memberService.checkIfUniqueEmail(email);
        // 既に登録されたメールアドレス → エラーメッセージを表示する
        if (!isUniqueEmailFlag) {
            model.addAttribute(REGIST_ERROR_EMAIL_MESSAGE_MODEL, PropertiesUtil.getProperties("regist.error.invalid.email"));
            return displayRegistInputPage();
        }

        final String password = registForm.getPassword();
        final String confirmationPassword = registForm.getConfirmationPassword();

        // パスワードと確認パスワードが一致 → 会員登録確認画面へ遷移する
        if (password.equals(confirmationPassword)) {
            final MemberDto memberDto = new MemberDto();
            BeanUtils.copyProperties(registForm, memberDto);
            session.setAttribute(SessionObjects.MEMBER, memberDto);
            return PageConstants.REGIST_CONFIRM_PAGE;
        // パスワードと確認パスワードが一致しない → エラーメッセージを表示する
        } else {
            model.addAttribute(REGIST_ERROR_PASSWORD_MESSAGE_MODEL, PropertiesUtil.getProperties("regist.error.invalid.password"));
            return displayRegistInputPage();
        }
    }

    /**
     * 会員登録処理を行う.
     *
     * @return 会員登録完了画面
     * @throws HummingException
     */
    @RequestMapping(value = "/regist/redirect")
    public String regist() throws HummingException {
        final MemberDto memberDto = (MemberDto) session.getAttribute(SessionObjects.MEMBER);
        if (memberDto == null) {
            return PageConstants.REGIST_INPUT_PAGE;
        }
        final MemberEntity memberEntity = new MemberEntity();
        BeanUtils.copyProperties(memberDto, memberEntity);
        memberService.regist(memberEntity);
        return "redirect:/member/regist/complete";
    }

    /**
     * 会員登録完了画面を表示する.
     *
     * @return 会員登録完了画面
     */
    @RequestMapping(value = "/regist/complete")
    public String displayRegistCompletePage() {
        return PageConstants.REGIST_COMPLETE_PAGE;
    }

    /**
     * 退会確認画面を表示する.
     *
     * @return 退会確認画面
     */
    @RequestMapping(value = "/withdraw/confirm")
    public String displayWithdrawConfirmPage() {
        if (session.getAttribute(SessionObjects.MEMBER) == null) {
            return PageConstants.LOGIN_PAGE;
        }
        return PageConstants.WITHDRAW_CONFIRM_PAGE;
    }

    /**
     * 退会処理を行う.
     *
     * @return 退会完了画面
     * @throws HummingException
     */
    @RequestMapping(value = "/withdraw/redirect")
    public String withdraw(WithdrawForm withdrawForm, SessionStatus sessionStatus) throws HummingException {
        if (session.getAttribute(SessionObjects.MEMBER) == null) {
            return PageConstants.LOGIN_PAGE;
        }
        memberService.withdraw(withdrawForm.getDeleted(), sessionStatus);
        return "redirect:/member/withdraw/complete";
    }

    /**
     * 退会完了画面を表示する.
     *
     * @return 退会完了画面
     */
    @RequestMapping(value = "/withdraw/complete")
    public String displayWithdrawCompletePage() {
        return PageConstants.WITHDRAW_COMPLETE_PAGE;
    }

    /**
     * マイページを表示する.
     *
     * @return マイページ
     */
    @RequestMapping(value = "/mypage")
    public String displayMyPage(Model model) {
        final MemberDto memberDto = (MemberDto) session.getAttribute(SessionObjects.MEMBER);
        if (memberDto == null) {
            return PageConstants.LOGIN_PAGE;
        }
        final MemberEntity memberEntity= new MemberEntity();
        BeanUtils.copyProperties(memberDto, memberEntity);

        final List<OrderDto> orderDtoList = orderService.getOrderHistory(memberEntity.getId());
        model.addAttribute(ORDER_LIST_MODEL, orderDtoList);
        model.addAttribute(ORDER_ITEM_LIST_MODEL, orderService.getOrderItemHistory(orderDtoList));

        return PageConstants.MY_PAGE;
    }
}