/**
 * This file is part of mycollab-web.
 *
 * mycollab-web is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * mycollab-web is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with mycollab-web.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.esofthead.mycollab.module.crm.view.lead;

import com.esofthead.mycollab.common.i18n.GenericI18Enum;
import com.esofthead.mycollab.module.crm.CrmTypeConstants;
import com.esofthead.mycollab.module.crm.domain.Lead;
import com.esofthead.mycollab.module.crm.i18n.LeadI18nEnum;
import com.esofthead.mycollab.module.crm.ui.CrmAssetsManager;
import com.esofthead.mycollab.vaadin.AppContext;
import com.esofthead.mycollab.vaadin.ui.*;
import com.vaadin.ui.*;

/**
 * 
 * @author MyCollab Ltd.
 * @since 2.0
 * 
 */
public class MassUpdateLeadWindow extends MassUpdateWindow<Lead> {
	private static final long serialVersionUID = 1L;

	public MassUpdateLeadWindow(final String title,
			final LeadListPresenter presenter) {
		super(title, CrmAssetsManager.getAsset(CrmTypeConstants.LEAD),
				new Lead(), presenter);
	}

	@Override
	protected IFormLayoutFactory buildFormLayoutFactory() {
		return new MassUpdateLeadFormLayoutFactory();
	}

	@Override
	protected AbstractBeanFieldGroupEditFieldFactory<Lead> buildBeanFormFieldFactory() {
		return new LeadEditFormFieldFactory<>(updateForm, false);
	}

	private class MassUpdateLeadFormLayoutFactory implements IFormLayoutFactory {
		private static final long serialVersionUID = 1L;

		private GridFormLayoutHelper informationLayout;
		private GridFormLayoutHelper addressLayout;

		@Override
		public ComponentContainer getLayout() {
			final VerticalLayout formLayout = new VerticalLayout();
			formLayout.setDefaultComponentAlignment(Alignment.TOP_CENTER);

			final Label organizationHeader = new Label(
					AppContext
							.getMessage(LeadI18nEnum.SECTION_LEAD_INFORMATION));
			organizationHeader.setStyleName(UIConstants.H2_STYLE2);
			formLayout.addComponent(organizationHeader);

			informationLayout = GridFormLayoutHelper.defaultFormLayoutHelper(2, 6);
			formLayout.addComponent(informationLayout.getLayout());

			addressLayout =  GridFormLayoutHelper.defaultFormLayoutHelper(2, 6);
			final Label leadMoreInfo = new Label(
					AppContext.getMessage(LeadI18nEnum.SECTION_ADDRESS));
			leadMoreInfo.setStyleName(UIConstants.H2_STYLE2);
			formLayout.addComponent(leadMoreInfo);
			formLayout.addComponent(addressLayout.getLayout());

			formLayout.addComponent(buildButtonControls());

			return formLayout;
		}

		// Title, Account Name, Lead Source, Industry, Status, Assign User,
		// primary/other city, primary/other state, primary/other postal
		// code, primary/other country
		@Override
		public void attachField(final Object propertyId, final Field<?> field) {

			if (propertyId.equals("title")) {
				this.informationLayout.addComponent(field,
						AppContext.getMessage(LeadI18nEnum.FORM_TITLE), 0, 0);
			} else if (propertyId.equals("accountname")) {
				this.informationLayout.addComponent(field,
						AppContext.getMessage(LeadI18nEnum.FORM_ACCOUNT_NAME),
						1, 0);
			} else if (propertyId.equals("source")) {
				this.informationLayout.addComponent(field,
						AppContext.getMessage(LeadI18nEnum.FORM_LEAD_SOURCE),
						0, 1);
			} else if (propertyId.equals("industry")) {
				this.informationLayout
						.addComponent(field, AppContext
								.getMessage(LeadI18nEnum.FORM_INDUSTRY), 1, 1);
			} else if (propertyId.equals("status")) {
				this.informationLayout.addComponent(field,
						AppContext.getMessage(LeadI18nEnum.FORM_STATUS), 0, 2);
			} else if (propertyId.equals("assignuser")) {
				this.informationLayout.addComponent(field, AppContext
						.getMessage(GenericI18Enum.FORM_ASSIGNEE), 1, 2);
			} else if (propertyId.equals("primcity")) {
				this.addressLayout.addComponent(field,
						AppContext.getMessage(LeadI18nEnum.FORM_PRIMARY_CITY),
						0, 0);
			} else if (propertyId.equals("primstate")) {
				this.addressLayout.addComponent(field,
						AppContext.getMessage(LeadI18nEnum.FORM_PRIMARY_STATE),
						0, 1);
			} else if (propertyId.equals("primpostalcode")) {
				this.addressLayout.addComponent(field, AppContext
						.getMessage(LeadI18nEnum.FORM_PRIMARY_POSTAL_CODE), 0,
						2);
			} else if (propertyId.equals("primcountry")) {
				this.addressLayout.addComponent(field, AppContext
						.getMessage(LeadI18nEnum.FORM_PRIMARY_COUNTRY), 0, 3);
			} else if (propertyId.equals("othercity")) {
				this.addressLayout.addComponent(field,
						AppContext.getMessage(LeadI18nEnum.FORM_OTHER_CITY), 1,
						0);
			} else if (propertyId.equals("otherstate")) {
				this.addressLayout.addComponent(field,
						AppContext.getMessage(LeadI18nEnum.FORM_OTHER_STATE),
						1, 1);
			} else if (propertyId.equals("otherpostalcode")) {
				this.addressLayout.addComponent(field, AppContext
						.getMessage(LeadI18nEnum.FORM_OTHER_POSTAL_CODE), 1, 2);
			} else if (propertyId.equals("othercountry")) {
				this.addressLayout.addComponent(field,
						AppContext.getMessage(LeadI18nEnum.FORM_OTHER_COUNTRY),
						1, 3);
			}
		}
	}
}
