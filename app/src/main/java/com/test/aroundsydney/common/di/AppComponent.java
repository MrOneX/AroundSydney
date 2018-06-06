package com.test.aroundsydney.common.di;

import com.test.aroundsydney.common.di.modules.ContextModule;
import com.test.aroundsydney.common.di.modules.LocationModule;
import com.test.aroundsydney.common.di.modules.RepositoryModule;
import com.test.aroundsydney.common.di.modules.RetrofitModule;
import com.test.aroundsydney.models.LocationModel;
import com.test.aroundsydney.models.repositories.local.LocalDBRepository;
import com.test.aroundsydney.models.repositories.remote.AmazonRepository;
import com.test.aroundsydney.presenters.DetailsPresenter;
import com.test.aroundsydney.presenters.ListPresenter;
import com.test.aroundsydney.presenters.MapPresenter;
import com.test.aroundsydney.ui.activities.MainActivity;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {ContextModule.class, LocationModule.class, RetrofitModule.class, RepositoryModule.class})
public interface AppComponent {

    void inject(MapPresenter presenter);

    void inject(ListPresenter presenter);

    void inject(DetailsPresenter presenter);

    void inject(LocationModel model);

    void inject(AmazonRepository repos);

    void inject(MainActivity activity);

    void inject(LocalDBRepository localDBRepository);
}

