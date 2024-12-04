# <img src="/assets/logo.svg" alt="logo" width="30"> HiRewards

HiRewards is a lightweight plugin that gives custom rewards when players greet a joining player with things like "welcome" or "hello!" The plugin aims to build a strong community by encouraging your player base to greet both new and returning players. The plugin is designed as a very supplementary and unintrusive cousin of the various "chat games" plugins out there.

## Features
- Custom rewards (compatible with any economy plugin)
- Different rewards for greeting new players & greeting returning players
- Custom trigger words (**supports regex**)
- Custom response window intervals
- Custom cooldown intervals
- Anti-exploit system

### Planned Features
- [ ] Welcoming streak (with increasing rewards)
- [ ] DiscordSRV integration
- [ ] ~~Fabric port (not planned, nor is any other port)~~

<details>
<summary>config.yml</summary>
  
```yaml
# Valid Greetings
# - The words which you would like to trigger the reward
# - Supports Regex
greetings:
  - ".*welcome*."
  - ".*hi*."
  - ".*h?ello*."
  - ".*what'?s up*."
  - ".*sup*."
  - ".*o/*."
  - ".*👋*."

# Response Time
# In SECONDS, amount of time all players have to greet an
# incoming player before they can no longer receive a reward.
response_time: 20

# Rewards
# Run as commands, rewards that are given to the player
# after a valid greeting.
rewards:
  firstjoin:
    sound:
      name: entity.experience_orb.pickup
      volume: 10
      pitch: 2
    commands:
      - "xp add %player% 25"
  join:
    sound:
      name: entity.experience_orb.pickup
      volume: 10
      pitch: 1.7
    commands:
      - "xp add %player% 10"

# Reward Cooldown
# In HOURS, the time required for player to receive reward for
# greeting the same player.
reward_cooldown: 8
```

</details>

---
HiRewards by riso

##### Special thanks to [AmberW](https://modrinth.com/user/AmberW ) (for Emojis in Icon Art)

##### This plugin is protected under the [CC BY-NC-SA 4.0](https://creativecommons.org/licenses/by-nc-sa/4.0/deed.en) license.
