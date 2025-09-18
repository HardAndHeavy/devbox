Rails.application.routes.draw do
  get "up" => "rails/health#show", as: :rails_health_check

  root to: ->(env) { [200, {}, ["This is the RoR framework in DevBox."]] }

  get '/qr', to: 'qr#generate', defaults: { format: :png }
end
