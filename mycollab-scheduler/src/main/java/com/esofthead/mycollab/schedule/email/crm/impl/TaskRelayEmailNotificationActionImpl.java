/**
 * This file is part of mycollab-scheduler.
 *
 * mycollab-scheduler is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * mycollab-scheduler is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with mycollab-scheduler.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.esofthead.mycollab.schedule.email.crm.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.esofthead.mycollab.common.domain.SimpleAuditLog;
import com.esofthead.mycollab.common.domain.SimpleRelayEmailNotification;
import com.esofthead.mycollab.common.i18n.GenericI18Enum;
import com.esofthead.mycollab.common.service.AuditLogService;
import com.esofthead.mycollab.core.utils.StringUtils;
import com.esofthead.mycollab.module.crm.CrmLinkGenerator;
import com.esofthead.mycollab.module.crm.CrmResources;
import com.esofthead.mycollab.module.crm.CrmTypeConstants;
import com.esofthead.mycollab.module.crm.domain.SimpleContact;
import com.esofthead.mycollab.module.crm.domain.SimpleTask;
import com.esofthead.mycollab.module.crm.i18n.TaskI18nEnum;
import com.esofthead.mycollab.module.crm.service.ContactService;
import com.esofthead.mycollab.module.crm.service.CrmNotificationSettingService;
import com.esofthead.mycollab.module.crm.service.TaskService;
import com.esofthead.mycollab.module.mail.MailUtils;
import com.esofthead.mycollab.module.mail.TemplateGenerator;
import com.esofthead.mycollab.module.user.AccountLinkUtils;
import com.esofthead.mycollab.module.user.domain.SimpleUser;
import com.esofthead.mycollab.module.user.service.UserService;
import com.esofthead.mycollab.schedule.email.ItemFieldMapper;
import com.esofthead.mycollab.schedule.email.MailContext;
import com.esofthead.mycollab.schedule.email.crm.TaskRelayEmailNotificationAction;
import com.esofthead.mycollab.schedule.email.format.DateFieldFormat;
import com.esofthead.mycollab.schedule.email.format.FieldFormat;
import com.esofthead.mycollab.schedule.email.format.html.TagBuilder;
import com.esofthead.mycollab.spring.ApplicationContextUtil;
import com.hp.gagawa.java.elements.A;
import com.hp.gagawa.java.elements.Img;

/**
 * 
 * @author MyCollab Ltd.
 * @since 1.0
 * 
 */
@Component
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class TaskRelayEmailNotificationActionImpl extends
		CrmDefaultSendingRelayEmailAction<SimpleTask> implements
		TaskRelayEmailNotificationAction {

	private static Logger log = LoggerFactory
			.getLogger(TaskRelayEmailNotificationActionImpl.class);

	@Autowired
	private AuditLogService auditLogService;
	@Autowired
	private TaskService taskService;

	@Autowired
	private CrmNotificationSettingService notificationService;

	private static final TaskFieldNameMapper mapper = new TaskFieldNameMapper();

	public TaskRelayEmailNotificationActionImpl() {
		super(CrmTypeConstants.TASK);
	}

	protected void setupMailHeaders(SimpleTask task,
			SimpleRelayEmailNotification emailNotification,
			TemplateGenerator templateGenerator) {

		String summary = task.getSubject();
		String summaryLink = CrmLinkGenerator.generateTaskPreviewFullLink(
				siteUrl, task.getId());

		templateGenerator.putVariable("makeChangeUser",
				emailNotification.getChangeByUserFullName());
		templateGenerator.putVariable("itemType", "task");
		templateGenerator.putVariable("summary", summary);
		templateGenerator.putVariable("summaryLink", summaryLink);
	}

	@Override
	protected TemplateGenerator templateGeneratorForCreateAction(
			MailContext<SimpleTask> context) {
		SimpleTask simpleTask = taskService.findById(context.getTypeid(),
				context.getSaccountid());
		if (simpleTask != null) {
			context.setWrappedBean(simpleTask);
			String subject = StringUtils.trim(simpleTask.getSubject(), 100);

			TemplateGenerator templateGenerator = new TemplateGenerator(
					context.getMessage(TaskI18nEnum.MAIL_CREATE_ITEM_SUBJECT,
							context.getChangeByUserFullName(), subject),
					context.templatePath("templates/email/crm/itemCreatedNotifier.mt"));
			setupMailHeaders(simpleTask, context.getEmailNotification(),
					templateGenerator);

			templateGenerator.putVariable("context", context);
			templateGenerator.putVariable("mapper", mapper);
			return templateGenerator;
		} else {
			return null;
		}
	}

	@Override
	protected TemplateGenerator templateGeneratorForUpdateAction(
			MailContext<SimpleTask> context) {
		SimpleTask simpleTask = taskService.findById(context.getTypeid(),
				context.getSaccountid());

		if (simpleTask == null) {
			return null;
		}

		context.setWrappedBean(simpleTask);
		String subject = StringUtils.trim(simpleTask.getSubject(), 100);

		TemplateGenerator templateGenerator = new TemplateGenerator(
				context.getMessage(TaskI18nEnum.MAIL_UPDATE_ITEM_SUBJECT,
						context.getChangeByUserFullName(), subject),
				context.templatePath("templates/email/crm/itemUpdatedNotifier.mt"));
		setupMailHeaders(simpleTask, context.getEmailNotification(),
				templateGenerator);

		if (context.getTypeid() != null) {
			SimpleAuditLog auditLog = auditLogService.findLatestLog(
					context.getTypeid(), context.getSaccountid());
			templateGenerator.putVariable("historyLog", auditLog);
			templateGenerator.putVariable("context", context);
			templateGenerator.putVariable("mapper", mapper);
		}
		return templateGenerator;
	}

	@Override
	protected TemplateGenerator templateGeneratorForCommentAction(
			MailContext<SimpleTask> context) {
		SimpleTask simpleTask = taskService.findById(context.getTypeid(),
				context.getSaccountid());

		if (simpleTask == null) {
			return null;
		}
		TemplateGenerator templateGenerator = new TemplateGenerator(
				context.getMessage(TaskI18nEnum.MAIL_COMMENT_ITEM_SUBJECT,
						context.getChangeByUserFullName(),
						StringUtils.trim(simpleTask.getSubject(), 100)),
				context.templatePath("templates/email/crm/itemAddNoteNotifier.mt"));
		setupMailHeaders(simpleTask, context.getEmailNotification(),
				templateGenerator);
		templateGenerator
				.putVariable("comment", context.getEmailNotification());

		return templateGenerator;
	}

	public static class TaskFieldNameMapper extends ItemFieldMapper {

		public TaskFieldNameMapper() {
			put("subject", TaskI18nEnum.FORM_SUBJECT, true);

			put("status", TaskI18nEnum.FORM_STATUS);
			put("startdate", new DateFieldFormat("startdate",
					TaskI18nEnum.FORM_START_DATE));

			put("assignuser", new AssigneeFieldFormat("assignuser",
					GenericI18Enum.FORM_ASSIGNEE));
			put("duedate", new DateFieldFormat("duedate",
					TaskI18nEnum.FORM_DUE_DATE));

			put("contactid", new ContactFieldFormat("contactid",
					TaskI18nEnum.FORM_CONTACT));
			put("priority", TaskI18nEnum.FORM_PRIORITY);

			// put("typeid", TaskI18nEnum.FORM_RELATED_TO, true);
			put("description", GenericI18Enum.FORM_DESCRIPTION, true);
		}
	}

	public static class ContactFieldFormat extends FieldFormat {

		public ContactFieldFormat(String fieldName, Enum displayName) {
			super(fieldName, displayName);
		}

		@Override
		public String formatField(MailContext<?> context) {
			SimpleTask task = (SimpleTask) context.getWrappedBean();
			if (task.getContactid() != null) {
				String contactIconLink = CrmResources
						.getResourceLink(CrmTypeConstants.CONTACT);
				Img img = TagBuilder.newImg("icon", contactIconLink);

				String contactLink = CrmLinkGenerator
						.generateContactPreviewFullLink(context.getSiteUrl(),
								task.getContactid());
				A link = TagBuilder.newA(contactLink, task.getContactName());
				return TagBuilder.newLink(img, link).write();
			} else {
				return "";
			}
		}

		@Override
		public String formatField(MailContext<?> context, String value) {
			if (value == null || "".equals(value)) {
				return "";
			}

			try {
				int contactId = Integer.parseInt(value);
				ContactService contactService = ApplicationContextUtil
						.getSpringBean(ContactService.class);
				SimpleContact contact = contactService.findById(contactId,
						context.getUser().getAccountId());

				if (contact != null) {
					String contactIconLink = CrmResources
							.getResourceLink(CrmTypeConstants.CONTACT);
					Img img = TagBuilder.newImg("icon", contactIconLink);
					String contactLink = CrmLinkGenerator
							.generateContactPreviewFullLink(
									context.getSiteUrl(), contact.getId());
					A link = TagBuilder.newA(contactLink,
							contact.getDisplayName());
					return TagBuilder.newLink(img, link).write();
				}
			} catch (Exception e) {
				log.error("Error", e);
			}

			return value;
		}

	}

	public static class AssigneeFieldFormat extends FieldFormat {

		public AssigneeFieldFormat(String fieldName, Enum displayName) {
			super(fieldName, displayName);
		}

		@Override
		public String formatField(MailContext<?> context) {
			SimpleTask task = (SimpleTask) context.getWrappedBean();
			if (task.getAssignuser() != null) {
				String userAvatarLink = MailUtils.getAvatarLink(
						task.getAssignUserAvatarId(), 16);

				Img img = TagBuilder.newImg("avatar", userAvatarLink);

				String userLink = AccountLinkUtils.generatePreviewFullUserLink(
						MailUtils.getSiteUrl(task.getSaccountid()),
						task.getAssignuser());
				A link = TagBuilder
						.newA(userLink, task.getAssignUserFullName());
				return TagBuilder.newLink(img, link).write();
			} else {
				return "";
			}

		}

		@Override
		public String formatField(MailContext<?> context, String value) {
			if (value == null || "".equals(value)) {
				return "";
			}

			UserService userService = ApplicationContextUtil
					.getSpringBean(UserService.class);
			SimpleUser user = userService.findUserByUserNameInAccount(value,
					context.getUser().getAccountId());
			if (user != null) {
				String userAvatarLink = MailUtils.getAvatarLink(
						user.getAvatarid(), 16);
				String userLink = AccountLinkUtils.generatePreviewFullUserLink(
						MailUtils.getSiteUrl(user.getAccountId()),
						user.getUsername());
				Img img = TagBuilder.newImg("avatar", userAvatarLink);
				A link = TagBuilder.newA(userLink, user.getDisplayName());
				return TagBuilder.newLink(img, link).write();
			}
			return value;
		}
	}

}